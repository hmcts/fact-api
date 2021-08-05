package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtAdditionalLink;

public interface CourtAdditionalLinkRepository extends JpaRepository<CourtAdditionalLink, Integer> {
}
