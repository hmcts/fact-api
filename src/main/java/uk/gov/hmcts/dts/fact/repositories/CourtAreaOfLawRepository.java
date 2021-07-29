package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLaw;

public interface CourtAreaOfLawRepository extends JpaRepository<CourtAreaOfLaw, Integer> {
    void deleteCourtAreaOfLawByCourtId(Integer id);
}
