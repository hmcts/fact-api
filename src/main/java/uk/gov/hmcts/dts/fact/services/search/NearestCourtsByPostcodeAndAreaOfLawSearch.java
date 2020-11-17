package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class NearestCourtsByPostcodeAndAreaOfLawSearch implements Search {

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    public NearestCourtsByPostcodeAndAreaOfLawSearch(final CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    @Override
    public List<CourtReferenceWithDistance> search(final MapitData mapitData, final String postcode, final String areaOfLaw) {
        return courtWithDistanceRepository.findNearestTenByAreaOfLaw(mapitData.getLat(), mapitData.getLon(), areaOfLaw)
            .stream()
            .map(CourtReferenceWithDistance::new)
            .collect(toList());
    }
}
