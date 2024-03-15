package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

/**
 * Default search for courts.
 */
@Component
public class DefaultSearch implements Search {

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    /**
     * Constructor for the DefaultSearch.
     * @param courtWithDistanceRepository The repository for court with distance
     */
    public DefaultSearch(final CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    /**
     * Search for courts.
     * @param serviceArea The service area
     * @param mapitData The mapit data
     * @param postcode The postcode
     * @param includeClosed A boolean indicating if closed courts should be included
     * @return A list of courts with distance
     */
    @Override
    public List<CourtWithDistance> searchWith(
        final ServiceArea serviceArea, final MapitData mapitData, final String postcode, final Boolean includeClosed) {
        return courtWithDistanceRepository.findNearestTenByAreaOfLaw(
            mapitData.getLat(),
            mapitData.getLon(),
            serviceArea.getAreaOfLaw().getName(),
            includeClosed
        );
    }
}
