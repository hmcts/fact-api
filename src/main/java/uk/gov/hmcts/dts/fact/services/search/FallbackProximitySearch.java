package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

/**
 * Fallback for proximity search.
 */
@Component
public class FallbackProximitySearch {

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    /**
     * Constructor for the FallbackProximitySearch.
     * @param courtWithDistanceRepository The repository for court with distance
     */
    public FallbackProximitySearch(final CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    /**
     * Fallback if the list of courts is empty.
     * @param courts The list of courts
     * @param areaOfLaw The area of law
     * @param includeClosed A boolean indicating if closed courts should be included
     * @param mapitData The mapit data
     * @return A list of courts with distance
     */
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
