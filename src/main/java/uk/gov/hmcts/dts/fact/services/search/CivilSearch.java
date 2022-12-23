package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class CivilSearch implements Search {

    private final CourtWithDistanceRepository courtWithDistanceRepository;
    private final FallbackProximitySearch fallbackProximitySearch;

    public CivilSearch(final CourtWithDistanceRepository courtWithDistanceRepository,
                       final FallbackProximitySearch fallbackProximitySearch) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
        this.fallbackProximitySearch = fallbackProximitySearch;
    }

    @Override
    public List<CourtWithDistance> searchWith(final ServiceArea serviceArea, final MapitData mapitData, final String postcode, final Boolean includeClosed) {

        final String areaOfLaw = serviceArea.getAreaOfLaw().getName();

        List<CourtWithDistance> courtsWithDistance = courtWithDistanceRepository
            .findNearestTenByAreaOfLawAndCourtPostcode(mapitData.getLat(), mapitData.getLon(), areaOfLaw, postcode);

        if (courtsWithDistance.isEmpty()) {
            final String postCodeMinusUnitCode = postcode.substring(0, postcode.length() - 2);
            courtsWithDistance = courtWithDistanceRepository
                .findNearestTenByAreaOfLawAndCourtPostcode(
                    mapitData.getLat(),
                    mapitData.getLon(),
                    areaOfLaw,
                    postCodeMinusUnitCode
                );
        }

        if (courtsWithDistance.isEmpty()) {
            final String outcode = postcode.substring(0, postcode.length() - 3);
            courtsWithDistance = courtWithDistanceRepository
                .findNearestTenByAreaOfLawAndCourtPostcode(
                    mapitData.getLat(),
                    mapitData.getLon(),
                    areaOfLaw,
                    outcode.trim()
                );
        }

        if (courtsWithDistance.isEmpty()) {
            final String areacode = postcode.split("\\d")[0];
            courtsWithDistance = courtWithDistanceRepository
                .findNearestTenByAreaOfLawAndCourtPostcode(mapitData.getLat(), mapitData.getLon(), areaOfLaw, areacode);
        }

        courtsWithDistance = fallbackProximitySearch.fallbackIfEmpty(courtsWithDistance, areaOfLaw, includeClosed, mapitData);

        return courtsWithDistance.stream().distinct().limit(10).collect(toList());
    }
}
