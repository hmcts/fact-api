package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class FamilyRegionalSearch implements Search {

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    public FamilyRegionalSearch(final CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    @Override
    public List<CourtWithDistance> searchWith(final ServiceArea serviceArea, final MapitData mapitData, final String postcode, final Boolean includeClosed) {

        final String areaOfLaw = serviceArea.getAreaOfLaw().getName();

        final List<CourtWithDistance> courtsWithDistance = mapitData.getLocalAuthority()
            .map(localAuthority -> courtWithDistanceRepository
                .findNearestRegionalByAreaOfLawAndLocalAuthority(mapitData.getLat(), mapitData.getLon(), areaOfLaw, localAuthority))
            .orElse(emptyList());

        return fallbackIfEmpty(courtsWithDistance, areaOfLaw, mapitData);
    }

    private List<CourtWithDistance> fallbackIfEmpty(final List<CourtWithDistance> courts,
                                                    final String areaOfLaw,
                                                    final MapitData mapitData) {
        if (courts.isEmpty()) {
            return courtWithDistanceRepository
                .findNearestRegionalByAreaOfLaw(mapitData.getLat(), mapitData.getLon(), areaOfLaw);
        }

        return courts;
    }
}
