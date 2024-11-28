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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for audit data.
 */
@Service
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public class AdminAuditService {

    private final AuditRepository auditRepository;
    private final AuditTypeRepository auditTypeRepository;

    /**
     * Constructor for the AdminAuditService.
     * @param auditRepository The repository for audit
     * @param auditTypeRepository The repository for audit type
     */
    @Autowired
    public AdminAuditService(AuditRepository auditRepository, AuditTypeRepository auditTypeRepository) {
        this.auditRepository = auditRepository;
        this.auditTypeRepository = auditTypeRepository;
    }

    /**
     * Get all audit data. Creation time will be returned as GMT/BST instead of UTC.
     * @param page The page number
     * @param size The page size
     * @param location The location
     * @param email The email
     * @param dateFrom The date from
     * @param dateTo The date to
     * @return A list of audit data
     */
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
            .map(audit -> {
                // Convert UTC LocalDateTime to ZonedDateTime for Europe/London
                ZonedDateTime creationTimeInUK = audit.getCreationTime()
                    .atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of("Europe/London")); // show as GMT/BST
                // Map to the DTO, adjusting the creation time
                uk.gov.hmcts.dts.fact.model.admin.Audit dto = new uk.gov.hmcts.dts.fact.model.admin.Audit(audit);
                dto.setCreationTime(creationTimeInUK.toLocalDateTime()); // Update creation time
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * Save audit data.
     * @param auditType The audit type
     * @param auditDataBefore The audit data before
     * @param auditDataAfter The audit data after
     * @param auditLocation The audit location
     */
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
