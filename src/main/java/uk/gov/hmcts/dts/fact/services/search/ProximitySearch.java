package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

/**
 * Proximity search for courts.
 */
@Component
public class ProximitySearch implements IProximitySearch {

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    /**
     * Constructor for the ProximitySearch.
     * @param courtWithDistanceRepository The repository for court with distance
     */
    public ProximitySearch(final CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    /**
     * Search for courts.
     * @param mapitData The mapit data
     * @return A list of 10 courts
     */
    @Override
    public List<CourtWithDistance> searchWith(final MapitData mapitData) {
        return courtWithDistanceRepository.findNearestTen(
            mapitData.getLat(),
            mapitData.getLon()
        );
    }
}
