package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtLocalAuthorityAreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;

import java.util.List;

public interface CourtPostcodeRepository extends JpaRepository<CourtPostcode, Integer> {
    CourtPostcode findByCourtIdAndPostcode(Integer courtId, String postcode);
}
