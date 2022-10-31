package uk.gov.hmcts.dts.fact.services.admin;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.LockExistsException;
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

    public CourtLock getCourtLock(String courtSlug) {
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockList =
            courtLockRepository.findCourtLockByCourtSlug(courtSlug);
        if(courtLockList.size() > 1) {
            throw new LockExistsException(String.format("More than one lock exists for multiple users: %s",
                                                        Arrays.toString(courtLockList.toArray())));
        }
        return new CourtLock(courtLockList.get(0));
    }

    public CourtLock addNewCourtLock(CourtLock courtLock) {
        // Check if the lock exists for the username/email
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockEntityList =
            courtLockRepository.findCourtLockByCourtSlug(courtLock.getCourtSlug());

        if(courtLockEntityList.size() > 1) {
            throw new LockExistsException(String.format("More than one lock exists for multiple users: %s",
                                                        Arrays.toString(courtLockEntityList.toArray())));
        }

        if (courtLockEntityList.size() > 0 && !courtLockEntityList.get(0).getUserEmail().equals(courtLock.getUserEmail())) {
            // If it doesn't, but we have a lock for the court, return with an error code
            throw new LockExistsException(String.format("Lock already exists for user: '%s' and court '%s'",
                                                        courtLock.getUserEmail(), courtLock.getCourtSlug()));
        } else {
            // If it exists, delete and update the resource
            courtLock.setLockAcquired(LocalDateTime.now(ZoneOffset.UTC));
            CourtLock savedCourtLock = new CourtLock(courtLockRepository.save(new uk.gov.hmcts.dts.fact.entity.CourtLock(courtLock)));
            adminAuditService.saveAudit(
                AuditType.findByName("Create court lock"),
                courtLockEntityList,
                savedCourtLock,
                courtLock.getCourtSlug());
            return savedCourtLock;
        }
    }

    public List<CourtLock> deleteCourtLock(String courtSlug, String userEmail) {
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockList =
            courtLockRepository.findCourtLockByCourtSlugAndUserEmail(courtSlug, userEmail);
        for (uk.gov.hmcts.dts.fact.entity.CourtLock courtLock: courtLockList) {
            courtLockRepository.delete(courtLock);
        }
        adminAuditService.saveAudit(
            AuditType.findByName("Delete court lock"),
            courtLockList,
            null,
            courtSlug);
        return courtLockList.stream().map(CourtLock::new).collect(Collectors.toList());
    }

    public void updateCourtLock(String courtSlug, String userEmail) {
        List<uk.gov.hmcts.dts.fact.entity.CourtLock> courtLockList =
            courtLockRepository.findCourtLockByCourtSlugAndUserEmail(courtSlug, userEmail);
        if(courtLockList.size() > 1) {
            throw new LockExistsException(String.format("More than one lock exists for multiple users: %s",
                                                        Arrays.toString(courtLockList.toArray())));
        }
        LocalDateTime newTime = LocalDateTime.now(ZoneOffset.UTC);
        adminAuditService.saveAudit(
            AuditType.findByName("Update court lock"),
            String.format("User %s has a lock acquired time before of: from %s",
                          courtLockList.get(0).getUserEmail(),
                          courtLockList.get(0).getLockAcquired()),
            String.format("User %s has a lock acquired time after of: from %s",
                          courtLockList.get(0).getUserEmail(),
                          newTime),
            courtSlug);
        courtLockList.get(0).setLockAcquired(newTime);
        courtLockRepository.save(courtLockList.get(0));
    }
}
