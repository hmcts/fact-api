package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.AuditType;

public interface AuditTypeRepository extends JpaRepository<AuditType, Integer> {
    AuditType findByName(String name);
}
