package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType;

import java.util.List;

public interface CourtSecondaryAddressTypeRepository extends JpaRepository<CourtSecondaryAddressType, Integer> {
    List<CourtSecondaryAddressType> findAllByAddressIdIn(List<Integer> addressId);
    void deleteAllByAddressIdIn(List<Integer> addressIds);
}
