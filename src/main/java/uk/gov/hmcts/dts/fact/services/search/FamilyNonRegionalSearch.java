package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class FamilyNonRegionalSearch implements Search {

    private final CourtWithDistanceRepository courtWithDistanceRepository;
    private final FallbackProximitySearch fallbackProximitySearch;

    public FamilyNonRegionalSearch(final CourtWithDistanceRepository courtWithDistanceRepository,
                                   final FallbackProximitySearch fallbackProximitySearch) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
        this.fallbackProximitySearch = fallbackProximitySearch;
    }

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
