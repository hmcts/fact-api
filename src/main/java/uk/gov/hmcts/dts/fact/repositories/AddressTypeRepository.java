package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.AddressType;

public interface AddressTypeRepository extends JpaRepository<AddressType, Integer> {
}
