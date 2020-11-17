package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Component
public class NearestTenByAreaOfLawAndLocalAuthoritySearch implements Search {

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    public NearestTenByAreaOfLawAndLocalAuthoritySearch(final CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    @Override
    public List<CourtReferenceWithDistance> search(final MapitData mapitData, final String postcode, final String areaOfLaw) {

        final Optional<String> localAuthority = mapitData.getLocalAuthority();

        return localAuthority.map(value -> courtWithDistanceRepository
            .findNearestTenByAreaOfLawAndLocalAuthority(mapitData.getLat(), mapitData.getLon(), areaOfLaw, value)
            .stream()
            .map(CourtReferenceWithDistance::new)
            .collect(toList()))
            .orElse(emptyList());
    }
}
