package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

@Component
public class DefaultSearch implements Search {

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    public DefaultSearch(final CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    @Override
    public List<CourtWithDistance> searchWith(final ServiceArea serviceArea, final MapitData mapitData, final String postcode) {
        return courtWithDistanceRepository.findNearestTenByAreaOfLaw(
            mapitData.getLat(),
            mapitData.getLon(),
            serviceArea.getAreaOfLaw().getName());
    }
}
