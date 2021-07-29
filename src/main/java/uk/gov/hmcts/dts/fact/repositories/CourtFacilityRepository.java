package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.Facility;


public interface CourtFacilityRepository extends JpaRepository<Facility, Integer> {

}
