package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.ContactType;

public interface ContactTypeRepository extends JpaRepository<ContactType, Integer> {
}
