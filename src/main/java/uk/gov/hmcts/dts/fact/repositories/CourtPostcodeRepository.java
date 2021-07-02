package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;

import java.util.List;

public interface CourtPostcodeRepository extends JpaRepository<CourtPostcode, Integer> {
    List<CourtPostcode> findByCourtIdAndPostcode(Integer courtId, String postcode);

    List<CourtPostcode> findByCourtIdAndPostcodeIn(Integer courtId, List<String> postcodes);

    List<CourtPostcode> deleteByCourtIdAndPostcode(Integer courtId, String postcode);
}
