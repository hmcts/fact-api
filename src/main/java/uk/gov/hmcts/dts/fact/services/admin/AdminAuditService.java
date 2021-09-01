package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.Audit;
import uk.gov.hmcts.dts.fact.repositories.AuditRepository;
import uk.gov.hmcts.dts.fact.repositories.AuditTypeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public class AdminAuditService {

    private final AuditRepository auditRepository;
    private final AuditTypeRepository auditTypeRepository;

    @Autowired
    public AdminAuditService(AuditRepository auditRepository, AuditTypeRepository auditTypeRepository) {
        this.auditRepository = auditRepository;
        this.auditTypeRepository = auditTypeRepository;
    }

    public List<uk.gov.hmcts.dts.fact.model.admin.Audit> getAllAuditData() {
        return auditRepository
            .findAll()
            .stream()
            .map(audit -> new uk.gov.hmcts.dts.fact.model.admin.Audit(
                audit.getId(),
                audit.getUserEmail(),
                audit.getAuditType(),
                audit.getActionDataBefore(),
                audit.getActionDataAfter(),
                audit.getLocation(),
                audit.getCreationTime()
            )).collect(Collectors.toList());
    }

    public void saveAudit(String auditType, String auditDataBefore, String auditDataAfter) {
        auditRepository.save(new Audit(
            SecurityContextHolder.getContext().getAuthentication().getName(),
            auditTypeRepository.findByName(auditType),
            auditDataBefore,
            auditDataAfter,
            LocalDateTime.now()
        ));
    }

    public void saveAudit(String auditType, String auditDataBefore, String auditDataAfter, String auditLocation) {
        auditRepository.save(new Audit(
            SecurityContextHolder.getContext().getAuthentication().getName(),
            auditTypeRepository.findByName(auditType),
            auditDataBefore,
            auditDataAfter,
            auditLocation,
            LocalDateTime.now()
        ));
    }
}
