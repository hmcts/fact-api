package uk.gov.hmcts.dts.fact.services.admin;

import feign.FeignException;
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

        if (courtLockEntityList.size() == 1 && !courtLockEntityList.get(0).getUserEmail().equals(courtUserEmail)) {
            // If it doesn't, but we have a lock for the court, return with an error code
            throw new LockExistsException(String.format("Lock for court '%s' is currently held by user '%s'",
                                                        courtSlug, courtLockEntityList.get(0).getUserEmail()
            ));
        } else if (courtLockEntityList.size() == 1 && courtLockEntityList.get(0).getUserEmail().equals(courtUserEmail)) {
            // If we have one row, and the name is the same as the incoming name, update it
            log.debug("Updating court lock for slug {} and user {}", courtSlug, courtUserEmail);
            return new CourtLock(updateCourtLock(courtSlug, courtUserEmail));
        } else if (courtLockEntityList.size() > 1) {
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
        if (courtLockList.size() == 0) {
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

    public uk.gov.hmcts.dts.fact.entity.CourtLock updateCourtLock(String courtSlug, String userEmail) {
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockList =
            courtLockRepository.findCourtLockByCourtSlugAndUserEmail(courtSlug, userEmail);
        if (courtLockList.size() > 1) {
            throw new LockExistsException(String.format(
                "More than one lock exists for multiple users: %s",
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
        return courtLockRepository.save(courtLockList.get(0));
    }
}
