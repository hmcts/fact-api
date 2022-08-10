package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType;

public interface CourtSecondaryAddressTypeRepository extends JpaRepository<CourtSecondaryAddressType, Integer> {
    void deleteAllByAddressId(Integer addressId);
}
