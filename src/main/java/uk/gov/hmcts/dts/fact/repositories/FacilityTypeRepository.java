package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.FacilityType;

import java.util.Optional;


public interface FacilityTypeRepository extends JpaRepository<FacilityType, Integer> {
    Optional<FacilityType> findByName(String name);

}
