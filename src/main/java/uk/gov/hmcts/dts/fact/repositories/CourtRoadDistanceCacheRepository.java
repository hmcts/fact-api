package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtRoadDistanceCache;

public interface CourtRoadDistanceCacheRepository extends JpaRepository<CourtRoadDistanceCache, String> {
}
