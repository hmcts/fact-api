package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;

import java.util.Optional;

public interface CourtPostcodeRepository extends JpaRepository<CourtPostcode, Integer> {
    Optional<CourtPostcode> findByCourtIdAndPostcode(Integer courtId, String postcode);
}
