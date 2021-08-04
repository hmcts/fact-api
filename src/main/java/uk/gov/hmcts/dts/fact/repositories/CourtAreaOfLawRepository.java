package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLaw;

import java.util.List;

public interface CourtAreaOfLawRepository extends JpaRepository<CourtAreaOfLaw, Integer> {
    void deleteCourtAreaOfLawByCourtId(Integer id);

    List<CourtAreaOfLaw> getCourtAreaOfLawByCourtId(Integer courtId);
}
