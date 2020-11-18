package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.CIVIL;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.FAMILY;

@Component
public class PostcodeSearchForServiceAreaRunner {

    private static final String LOCAL_AUTHORITY = "local-authority";
    private static final String REGIONAL = "regional";

    private final CourtWithDistanceRepository courtWithDistanceRepository;

    public PostcodeSearchForServiceAreaRunner(CourtWithDistanceRepository courtWithDistanceRepository) {
        this.courtWithDistanceRepository = courtWithDistanceRepository;
    }

    public List<CourtReferenceWithDistance> getSearchFor(final ServiceArea serviceArea,
                                                         final String postcode,
                                                         final MapitData mapitData) {

        final String type = serviceArea.getType();
        final String areaOfLaw = serviceArea.getAreaOfLaw().getName();
        final Double lat = mapitData.getLat();
        final Double lon = mapitData.getLon();

        if (FAMILY.isEqualTo(type)) {

            final Optional<String> localAuthorityOptional = mapitData.getLocalAuthority();
            
            if (LOCAL_AUTHORITY.equals(serviceArea.getCatchmentMethod())
                && localAuthorityOptional.isPresent()) {

                final String localAuthority = localAuthorityOptional.get();

                if (isRegional(serviceArea)) {

                    final List<CourtWithDistance> courtsWithDistance = courtWithDistanceRepository
                        .findNearestRegionalByAreaOfLawAndLocalAuthority(lat, lon, areaOfLaw, localAuthority);

                    return fallbackProximityRegionalSearchIfEmpty(courtsWithDistance, areaOfLaw, mapitData);
                } else {

                    final List<CourtWithDistance> courtsWithDistance = courtWithDistanceRepository
                        .findNearestTenByAreaOfLawAndLocalAuthority(lat, lon, areaOfLaw, localAuthority);

                    return fallbackProximitySearchIfEmpty(courtsWithDistance, areaOfLaw, mapitData);
                }
            }

        } else if (CIVIL.isEqualTo(type)) {

            final List<CourtWithDistance> courtsWithDistance = courtWithDistanceRepository
                .findNearestTenByAreaOfLawAndCourtPostcode(lat, lon, areaOfLaw, postcode);

            return fallbackProximitySearchIfEmpty(courtsWithDistance, areaOfLaw, mapitData);
        }

        return convert(courtWithDistanceRepository.findNearestTenByAreaOfLaw(lat, lon, areaOfLaw));
    }

    private boolean isRegional(final ServiceArea serviceArea) {
        return serviceArea.getServiceAreaCourts().stream()
            .anyMatch(serviceAreaCourt -> REGIONAL.equals(serviceAreaCourt.getCatchmentType()));
    }

    private List<CourtReferenceWithDistance> fallbackProximityRegionalSearchIfEmpty(final List<CourtWithDistance> courts,
                                                                                    final String areaOfLaw,
                                                                                    final MapitData mapitData) {
        if (courts.isEmpty()) {
            return convert(courtWithDistanceRepository
                .findNearestRegionalByAreaOfLaw(mapitData.getLat(), mapitData.getLon(), areaOfLaw));
        }

        return convert(courts);
    }

    private List<CourtReferenceWithDistance> fallbackProximitySearchIfEmpty(final List<CourtWithDistance> courts,
                                                                            final String areaOfLaw,
                                                                            final MapitData mapitData) {
        if (courts.isEmpty()) {
            return convert(courtWithDistanceRepository
                .findNearestTenByAreaOfLaw(mapitData.getLat(), mapitData.getLon(), areaOfLaw));
        }

        return convert(courts);
    }

    private List<CourtReferenceWithDistance> convert(final List<CourtWithDistance> courtsWithDistance) {
        return courtsWithDistance.stream()
            .map(CourtReferenceWithDistance::new)
            .collect(toList());
    }
}
