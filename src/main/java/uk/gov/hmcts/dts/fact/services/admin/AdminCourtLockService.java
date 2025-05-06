package uk.gov.hmcts.dts.fact.services.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.exception.LockExistsException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtLock;
import uk.gov.hmcts.dts.fact.repositories.CourtLockRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections; // Ensure this import is present
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for admin court lock data.
 */
@Service
@Slf4j
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class AdminCourtLockService {
    private final CourtLockRepository courtLockRepository;
    private final AdminAuditService adminAuditService;
    private static final int LOCK_AMOUNT_PER_COURT = 1;

    /**
     * Constructor for the AdminCourtLockService.
     * @param courtLockRepository The repository for court lock
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminCourtLockService(final CourtLockRepository courtLockRepository,
                                 final AdminAuditService adminAuditService) {
        this.courtLockRepository = courtLockRepository;
        this.adminAuditService = adminAuditService;
    }

    /**
     *
     * <p>Get all court locks from the database that match a provided court slug.</p>
     *
     * <p> Important to note is at the moment one lock is used per court, but this is
     * being returned as a list to accommodate for a future change where more than one
     * lock may be required instead. </p>
     *
     * @param courtSlug the court slug.
     * @return a list of court locks.
     */
    public List<CourtLock> getCourtLocks(String courtSlug) {
        return courtLockRepository
            .findCourtLockByCourtSlug(courtSlug)
            .stream()
            .map(CourtLock::new)
            .collect(Collectors.toList());
    }

    /**
     * <p>Add the new court lock to the database.</p>
     *
     * <p>Exceptions will occur if a lock already exists for a court and a different user is
     * attempting to retrieve that lock (by calling this method).</p>
     *
     * <p>If the same user is hitting the logic that calls this method, then we simply update their
     * timestamp on the database by calling updateCourtLock.</p>
     *
     * <p>Otherwise we add them to the database</p>
     *
     * @param courtLock the new CourtLock object to add to the database.
     * @return The new CourtLock object that has been added to the database.
     */
    public CourtLock addNewCourtLock(CourtLock courtLock) {
        String courtSlug = courtLock.getCourtSlug();
        String courtUserEmail = courtLock.getUserEmail();
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockEntityList =
            courtLockRepository.findCourtLockByCourtSlug(courtSlug);
        int courtListSize = courtLockEntityList.size();

        if (courtListSize == LOCK_AMOUNT_PER_COURT && !courtLockEntityList.get(0).getUserEmail().equals(courtUserEmail)) {
            // If the name doesn't match, but we have a lock for the court, return with an error code
            throw new LockExistsException(String.format("Lock for court '%s' is currently held by user '%s'",
                                                        courtSlug, courtLockEntityList.get(0).getUserEmail()
            ));
        } else if (courtListSize == LOCK_AMOUNT_PER_COURT && courtLockEntityList.get(0).getUserEmail().equals(courtUserEmail)) {
            // If we have one row, and the name is the same as the incoming name, update it
            log.debug("Updating court lock for slug {} and user {}", courtSlug, courtUserEmail);
            return updateCourtLock(courtSlug, courtUserEmail);
        } else if (courtListSize > LOCK_AMOUNT_PER_COURT) {
            throw new LockExistsException(String.format(
                "More than one lock exists for %s: %s",
                courtSlug,
                Arrays.toString(courtLockEntityList.toArray())
            ));
        } else {
            // If it exists, delete and update the resource
            log.debug("Creating court lock for slug {} and user {}", courtSlug, courtUserEmail);
            courtLock.setLockAcquired(LocalDateTime.now(ZoneOffset.UTC));
            // Ensure we pass the correct object type to the repository save method
            uk.gov.hmcts.dts.fact.entity.CourtLock entityToSave = new uk.gov.hmcts.dts.fact.entity.CourtLock(courtLock);
            uk.gov.hmcts.dts.fact.entity.CourtLock savedEntity = courtLockRepository.save(entityToSave);
            CourtLock savedCourtLock = new CourtLock(savedEntity); // Map the saved *entity* back to the DTO

            adminAuditService.saveAudit(
                AuditType.findByName("Create court lock"),
                null, // For create, 'before' data is usually null
                savedCourtLock, // Audit the DTO representing the created state
                courtLock.getCourtSlug()
            );
            return savedCourtLock; // Return the DTO
        }
    }

    /**
     *
     * <p>Remove the court lock provided using both a slug and an email.</p>
     *
     * <p>This is primarily used if two people are interacting with a court.</p>
     *
     * <p>The frontend logic for this is as follows:
     *      Person one comes in, makes a change and leaves. 20 minutes passes.
     *      Person two then comes in, clicks on the court and because the time has passed the
     *      threshold, for that specific court only, person 2 gets the lock, and person 1 loses it.</p>
     *
     * @param courtSlug the court slug.
     * @param userEmail the users email, for example moshcat@justice.cat.meow.
     * @return A list of court locks that have been deleted from the database.
     */
    public List<CourtLock> deleteCourtLock(String courtSlug, String userEmail) {
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockList =
            courtLockRepository.findCourtLockByCourtSlugAndUserEmail(courtSlug, userEmail);
        if (courtLockList.isEmpty()) {
            throw new NotFoundException(String.format("No lock found for court: %s and user: %s",
                                                      courtSlug, userEmail
            ));
        }
        // This method still uses fetch-then-delete. If it causes issues later,
        // it might need the same direct delete treatment.
        for (uk.gov.hmcts.dts.fact.entity.CourtLock courtLock : courtLockList) {
            courtLockRepository.delete(courtLock);
        }
        adminAuditService.saveAudit(
            AuditType.findByName("Delete court lock"),
            courtLockList, // Auditing the list of entities before they were deleted
            null,
            courtSlug
        );
        // Map the list of deleted entities back to DTOs for the return value
        return courtLockList.stream().map(CourtLock::new).collect(Collectors.toList());
    }

    // ================================================================================
    // START OF CORRECTED deleteCourtLockByEmail METHOD
    // ================================================================================
    /**
     *
     * <p>Delete the court lock by email.</p>
     *
     * <p>The frontend logic for this is when a user clicks the logout button, or when they log in.
     * At that point, we would want no locks to be assigned to them.</p>
     *
     * @param userEmail the users email.
     * @return A list of court locks that have been removed (or an empty list if none were found).
     */
    @Transactional // Keep transactional for atomicity of find, delete, audit
    public List<CourtLock> deleteCourtLockByEmail(String userEmail) {
        // 1. Find the locks first to know what *was* there for audit and return value
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> locksToDelete = courtLockRepository.findByUserEmail(userEmail); // Use the method defined in the updated repository

        if (!locksToDelete.isEmpty()) {
            log.info("Found {} lock(s) for user {}. Attempting direct deletion.", locksToDelete.size(), userEmail);
            // 2. Perform the direct delete using the new repository method
            int deletedCount = courtLockRepository.deleteDirectByUserEmail(userEmail); // Use the method defined in the updated repository
            log.info("Direct delete operation removed {} lock(s) for user {}.", deletedCount, userEmail);

            // 3. Audit the deletion using the list fetched *before* the delete
            adminAuditService.saveAudit(
                AuditType.findByName("Delete court lock"),
                locksToDelete, // Use the state before deletion for audit 'before' data
                null,
                null // Slug is not relevant when deleting by email only
            );

            // 4. Return the list of locks that were found (and subsequently deleted)
            return locksToDelete.stream().map(CourtLock::new).collect(Collectors.toList());
        } else {
            log.warn("No court locks found for user {} to delete.", userEmail);
            // If no locks were found, nothing to delete or audit
            return Collections.emptyList(); // Return an empty list explicitly
        }
    }
    // ================================================================================
    // END OF CORRECTED deleteCourtLockByEmail METHOD
    // ================================================================================


    /**
     *
     * <p>Update the court locks timestamp.</p>
     *
     * <p>Used when a user is editing a court and performs actions on selected resources.
     * For example, if they are updating the opening hours of a court.</p>
     *
     * <p>The timeout for a lock is based upon the most recent action time of the user who has it allocated to them.
     * This is to ensure that if a user quits the admin portal by closing their browser, that
     * the lock is not indefinitely attributed to them.</p>
     *
     * @param courtSlug the court slug.
     * @param userEmail the users email, for example kupocat@justice.cat.meow.
     * @return The updated court lock object.
     */
    public CourtLock updateCourtLock(String courtSlug, String userEmail) {
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockList =
            courtLockRepository.findCourtLockByCourtSlugAndUserEmail(courtSlug, userEmail);

        if (courtLockList.isEmpty()) {
            // If a user attempts to update a lock, and no row exists for the slug at all,
            // apply the lock to them
            log.debug("When updating user: {}, no row was found. Creating lock now for court: {}",
                      userEmail, courtSlug
            );
            // Ensure we create the entity correctly before passing to addNewCourtLock if its signature expects a DTO
            CourtLock newLockDto = new CourtLock(
                LocalDateTime.now(ZoneOffset.UTC),
                courtSlug,
                userEmail
            );
            return addNewCourtLock(newLockDto); // Assuming addNewCourtLock handles the DTO->Entity conversion and save

        } else if (courtLockList.size() > LOCK_AMOUNT_PER_COURT) {
            throw new LockExistsException(String.format(
                "More than one lock exists for %s: %s",
                courtSlug,
                Arrays.toString(courtLockList.toArray())
            ));
        }

        // If we reach here, courtLockList contains exactly one entity to update
        uk.gov.hmcts.dts.fact.entity.CourtLock lockToUpdate = courtLockList.get(0);
        LocalDateTime oldTime = lockToUpdate.getLockAcquired(); // Capture old time if needed for audit comparison
        LocalDateTime newTime = LocalDateTime.now(ZoneOffset.UTC);

        // Capture state *before* the update for audit, perhaps using the DTO constructor
        CourtLock stateBefore = new CourtLock(lockToUpdate);

        // Update the entity's timestamp
        lockToUpdate.setLockAcquired(newTime);

        // Save the updated entity
        uk.gov.hmcts.dts.fact.entity.CourtLock savedEntity = courtLockRepository.save(lockToUpdate);

        // Capture state *after* the update for audit, using the saved entity
        CourtLock stateAfter = new CourtLock(savedEntity);

        // Perform audit - Adjust audit parameters based on AdminAuditService expectations
        // This example audits the DTO representations of the state before/after.
        adminAuditService.saveAudit(
            AuditType.findByName("Update court lock"),
            stateBefore, // State before update
            stateAfter,  // State after update
            courtSlug
        );

        // Return the DTO representing the updated state
        return stateAfter;
    }
}
