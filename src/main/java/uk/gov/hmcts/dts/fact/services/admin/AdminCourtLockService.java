package uk.gov.hmcts.dts.fact.services.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.LockExistsException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtLock;
import uk.gov.hmcts.dts.fact.repositories.CourtLockRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class AdminCourtLockService {
    private final CourtLockRepository courtLockRepository;
    private final AdminAuditService adminAuditService;
    private static final int LOCK_AMOUNT_PER_COURT = 1;

    @Autowired
    public AdminCourtLockService(final CourtLockRepository courtLockRepository,
                                 final AdminAuditService adminAuditService) {
        this.courtLockRepository = courtLockRepository;
        this.adminAuditService = adminAuditService;
    }

    public List<CourtLock> getCourtLocks(String courtSlug) {
        return courtLockRepository
            .findCourtLockByCourtSlug(courtSlug)
            .stream()
            .map(CourtLock::new)
            .collect(Collectors.toList());
    }

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
            CourtLock savedCourtLock = new CourtLock(courtLockRepository.save(new uk.gov.hmcts.dts.fact.entity.CourtLock(
                courtLock)));
            adminAuditService.saveAudit(
                AuditType.findByName("Create court lock"),
                courtLockEntityList,
                savedCourtLock,
                courtLock.getCourtSlug()
            );
            return savedCourtLock;
        }
    }

    public List<CourtLock> deleteCourtLock(String courtSlug, String userEmail) {
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockList =
            courtLockRepository.findCourtLockByCourtSlugAndUserEmail(courtSlug, userEmail);
        if (courtLockList.isEmpty()) {
            throw new NotFoundException(String.format("No lock found for court: %s and user: %s",
                                                      courtSlug, userEmail
            ));
        }
        for (uk.gov.hmcts.dts.fact.entity.CourtLock courtLock : courtLockList) {
            courtLockRepository.delete(courtLock);
        }
        adminAuditService.saveAudit(
            AuditType.findByName("Delete court lock"),
            courtLockList,
            null,
            courtSlug
        );
        return courtLockList.stream().map(CourtLock::new).collect(Collectors.toList());
    }

    public List<CourtLock> deleteCourtLockByEmail(String userEmail) {
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockList =
            courtLockRepository.deleteAllByUserEmail(userEmail);
        adminAuditService.saveAudit(
            AuditType.findByName("Delete court lock"),
            courtLockList,
            null,
            null
        );
        return courtLockList.stream().map(CourtLock::new).collect(Collectors.toList());
    }

    public CourtLock updateCourtLock(String courtSlug, String userEmail) {
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockList =
            courtLockRepository.findCourtLockByCourtSlugAndUserEmail(courtSlug, userEmail);

        if (courtLockList.isEmpty()) {
            // If a user attempts to update a lock, and no row exists for the slug at all,
            // apply the lock to them
            log.debug("When updating user: {}, no row was found. Creating lock now for court: {}",
                      userEmail, courtSlug
            );
            return addNewCourtLock(new CourtLock(
                LocalDateTime.now(ZoneOffset.UTC),
                courtSlug,
                userEmail
            ));
        } else if (courtLockList.size() > LOCK_AMOUNT_PER_COURT) {
            throw new LockExistsException(String.format(
                "More than one lock exists for %s: %s",
                courtSlug,
                Arrays.toString(courtLockList.toArray())
            ));
        }
        LocalDateTime newTime = LocalDateTime.now(ZoneOffset.UTC);
        adminAuditService.saveAudit(
            AuditType.findByName("Update court lock"),
            String.format(
                "User %s has a lock acquired time (before) of: %s",
                courtLockList.get(0).getUserEmail(),
                courtLockList.get(0).getLockAcquired()
            ),
            String.format(
                "User %s has a lock acquired time (after) of: %s",
                courtLockList.get(0).getUserEmail(),
                newTime
            ),
            courtSlug
        );
        courtLockList.get(0).setLockAcquired(newTime);
        return new CourtLock(courtLockRepository.save(courtLockList.get(0)));
    }
}
