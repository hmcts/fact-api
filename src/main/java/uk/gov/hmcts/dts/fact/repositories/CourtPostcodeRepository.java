package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;

public interface CourtPostcodeRepository extends JpaRepository<CourtPostcode, Integer> {
}
