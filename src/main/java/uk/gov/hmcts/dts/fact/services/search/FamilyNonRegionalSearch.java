package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Search for family courts.
 */
@Component
public class FamilyNonRegionalSearch implements Search {

    private final CourtWithDistanceRepository courtWithDistanceRepository;
    private final FallbackProximitySearch fallbackProximitySearch;

    /**
     * Constructor for the FamilyNonRegionalSearch.
     * @param courtWithDistanceRepository The repository for court with distance
     * @param fallbackProximitySearch The fallback proximity search
     */
    public FamilyNonRegionalSearch(final CourtWithDistanceRepository courtWithDistanceRepository,
                                   final FallbackProximitySearch fallbackProximitySearch) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
        this.fallbackProximitySearch = fallbackProximitySearch;
    }

    /**
     * Search for family courts.
     * @param serviceArea The service area
     * @param mapitData The mapit data
     * @param postcode The postcode
     * @param includeClosed A boolean indicating if closed courts should be included
     * @return A list of courts with distance
     */
    @Override
    public List<CourtWithDistance> searchWith(final ServiceArea serviceArea, final MapitData mapitData, final String postcode, final Boolean includeClosed) {

        final String areaOfLaw = serviceArea.getAreaOfLaw().getName();

        final List<CourtWithDistance> courtsWithDistance = mapitData.getLocalAuthority()
            .map(localAuthority -> courtWithDistanceRepository
                .findNearestTenByAreaOfLawAndLocalAuthority(mapitData.getLat(), mapitData.getLon(), areaOfLaw, localAuthority, includeClosed))
            .orElse(emptyList());

        return fallbackProximitySearch.fallbackIfEmpty(courtsWithDistance, areaOfLaw, includeClosed, mapitData);
    }
}
