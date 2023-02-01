package uk.gov.hmcts.dts.fact.services.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.Audit;
import uk.gov.hmcts.dts.fact.repositories.AuditRepository;
import uk.gov.hmcts.dts.fact.repositories.AuditTypeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    public List<uk.gov.hmcts.dts.fact.model.admin.Audit> getAllAuditData(int page, int size,
                                                                         Optional<String> location,
                                                                         Optional<String> email,
                                                                         Optional<LocalDateTime> dateFrom,
                                                                         Optional<LocalDateTime> dateTo) {

        Page<Audit> auditPage = dateFrom.isPresent() && dateTo.isPresent()
            ? auditRepository.findAllByLocationContainingAndUserEmailContainingAndCreationTimeBetweenOrderByCreationTimeDesc(
            location.orElse(""), email.orElse(""), dateFrom.get(), dateTo.get(), PageRequest.of(page, size))
            : auditRepository.findAllByLocationContainingAndUserEmailContainingOrderByCreationTimeDesc(
            location.orElse(""), email.orElse(""), PageRequest.of(page, size));

        return auditPage
            .stream()
            .map(uk.gov.hmcts.dts.fact.model.admin.Audit::new)
            .collect(Collectors.toList());
    }

    @SneakyThrows
    public void saveAudit(String auditType, Object auditDataBefore, Object auditDataAfter, String auditLocation) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        auditRepository.save(new Audit(
            SecurityContextHolder.getContext().getAuthentication().getName(),
            auditTypeRepository.findByName(auditType),
            objectMapper.writeValueAsString(auditDataBefore),
            objectMapper.writeValueAsString(auditDataAfter),
            auditLocation,
            LocalDateTime.now()
        ));
    }
}
