package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Search for family regional courts.
 */
@Component
public class FamilyRegionalSearch implements Search {

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    /**
     * Constructor for the FamilyRegionalSearch.
     * @param courtWithDistanceRepository The repository for court with distance
     */
    public FamilyRegionalSearch(final CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    /**
     * Search for family regional courts.
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
                .findNearestRegionalByAreaOfLawAndLocalAuthority(mapitData.getLat(), mapitData.getLon(), areaOfLaw, localAuthority))
            .orElse(emptyList());

        return fallbackIfEmpty(courtsWithDistance, areaOfLaw, mapitData);
    }

    /**
     * Fallback if the list of courts is empty.
     * @param courts The list of courts
     * @param areaOfLaw The area of law
     * @param mapitData The mapit data
     * @return A list of courts with distance
     */
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
