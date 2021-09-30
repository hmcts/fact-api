package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.Facility;

public interface FacilityRepository extends JpaRepository<Facility, Integer> {
}
