package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

@Component
public class ProximitySearch implements IProximitySearch {

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    public ProximitySearch(final CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    @Override
    public List<CourtWithDistance> searchWith(final MapitData mapitData) {
        return courtWithDistanceRepository.findNearestTen(
            mapitData.getLat(),
            mapitData.getLon()
        );
    }
}
