package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.Audit;

import java.time.LocalDateTime;

public interface AuditRepository extends JpaRepository<Audit, Long> {

    Page<Audit> findAllByLocationContainingAndUserEmailContainingOrderByCreationTimeDesc(String location, String email, Pageable pageable);

    Page<Audit> findAllByLocationContainingAndUserEmailContainingAndCreationTimeBetweenOrderByCreationTimeDesc(String location, String email,
                                                                                                               LocalDateTime dateFrom, LocalDateTime dateTo,
                                                                                                               Pageable pageable);
}
