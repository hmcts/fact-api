package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtFacility;

import java.util.List;


public interface CourtFacilityRepository extends JpaRepository<CourtFacility, Integer> {
    List<CourtFacility> findByCourtId(Integer courtId);

}
