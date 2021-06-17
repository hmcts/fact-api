package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtLocalAuthorityAreaOfLaw;

import java.util.List;


public interface CourtLocalAuthorityAreaOfLawRepository extends JpaRepository<CourtLocalAuthorityAreaOfLaw, Integer> {
    List<CourtLocalAuthorityAreaOfLaw> findByCourtId(Integer courtId);

}
