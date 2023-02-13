package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

@Component
public class FallbackProximitySearch {

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    public FallbackProximitySearch(final CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    public List<CourtWithDistance> fallbackIfEmpty(final List<CourtWithDistance> courts,
                                                   final String areaOfLaw,
                                                   final Boolean includeClosed,
                                                   final MapitData mapitData) {
        if (courts.isEmpty()) {
            return courtWithDistanceRepository
                .findNearestTenByAreaOfLaw(mapitData.getLat(), mapitData.getLon(), areaOfLaw, includeClosed);
        }

        return courts;
    }
}
