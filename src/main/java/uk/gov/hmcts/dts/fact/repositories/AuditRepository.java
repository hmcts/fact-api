package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.Audit;

public interface AuditRepository extends JpaRepository<Audit, Integer> {
}
