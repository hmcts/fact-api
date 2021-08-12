package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtAdditionalLink;

import java.util.List;

public interface CourtAdditionalLinkRepository extends JpaRepository<CourtAdditionalLink, Integer> {

    void deleteCourtAdditionalLinksByCourtId(Integer courtId);

    List<CourtAdditionalLink> findCourtAdditionalLinksByCourtId(Integer courtId);
}
