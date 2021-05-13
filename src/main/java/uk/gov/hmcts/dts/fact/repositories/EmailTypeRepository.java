package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.EmailType;

public interface EmailTypeRepository extends JpaRepository<EmailType, Integer> {
}
