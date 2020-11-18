package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;

import java.util.List;

import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.CIVIL;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.FAMILY;

@Component
public class PostcodeSearchForServiceAreaRunner {

    private static final String LOCAL_AUTHORITY = "local-authority";
    private static final String REGIONAL = "regional";

    private final NearestRegionalByAreaOfLawAndLocalAuthoritySearch nearestRegionalByAreaOfLawAndLocalAuthoritySearch;
    private final NearestTenByAreaOfLawAndLocalAuthoritySearch nearestTenByAreaOfLawAndLocalAuthoritySearch;
    private final NearestCourtsByCourtPostcodeAndAreaOfLawSearch nearestCourtsByCourtPostcodeAndAreaOfLawSearch;
    private final NearestCourtsByPostcodeAndAreaOfLawSearch nearestCourtsByPostcodeAndAreaOfLawSearch;
    private final NearestRegionalByAreaOfLawSearch nearestRegionalByAreaOfLawSearch;

    public PostcodeSearchForServiceAreaRunner(final NearestRegionalByAreaOfLawAndLocalAuthoritySearch nearestRegionalByAreaOfLawAndLocalAuthoritySearch,
                                              final NearestTenByAreaOfLawAndLocalAuthoritySearch nearestTenByAreaOfLawAndLocalAuthoritySearch,
                                              final NearestCourtsByCourtPostcodeAndAreaOfLawSearch nearestCourtsByCourtPostcodeAndAreaOfLawSearch,
                                              final NearestCourtsByPostcodeAndAreaOfLawSearch nearestCourtsByPostcodeAndAreaOfLawSearch,
                                              final NearestRegionalByAreaOfLawSearch nearestRegionalByAreaOfLawSearch) {
        this.nearestRegionalByAreaOfLawAndLocalAuthoritySearch = nearestRegionalByAreaOfLawAndLocalAuthoritySearch;
        this.nearestTenByAreaOfLawAndLocalAuthoritySearch = nearestTenByAreaOfLawAndLocalAuthoritySearch;
        this.nearestCourtsByCourtPostcodeAndAreaOfLawSearch = nearestCourtsByCourtPostcodeAndAreaOfLawSearch;
        this.nearestCourtsByPostcodeAndAreaOfLawSearch = nearestCourtsByPostcodeAndAreaOfLawSearch;
        this.nearestRegionalByAreaOfLawSearch = nearestRegionalByAreaOfLawSearch;
    }

    public List<CourtReferenceWithDistance> getSearchFor(final ServiceArea serviceArea,
                                                         final String postcode,
                                                         final MapitData mapitData) {

        final String type = serviceArea.getType();

        if (FAMILY.isEqualTo(type)) {

            if (LOCAL_AUTHORITY.equals(serviceArea.getCatchmentMethod())
                && mapitData.getLocalAuthority().isPresent()) {

                if (isRegional(serviceArea)) {

                    final List<CourtReferenceWithDistance> courts = nearestRegionalByAreaOfLawAndLocalAuthoritySearch
                        .search(mapitData, postcode, serviceArea.getAreaOfLaw().getName());

                    return fallbackProximityRegionalSearchIfEmpty(courts, serviceArea, postcode, mapitData);
                } else {

                    final List<CourtReferenceWithDistance> courts = nearestTenByAreaOfLawAndLocalAuthoritySearch
                        .search(mapitData, postcode, serviceArea.getAreaOfLaw().getName());

                    return fallbackProximitySearchIfEmpty(courts, serviceArea, postcode, mapitData);
                }
            }

        } else if (CIVIL.isEqualTo(type)) {
            final List<CourtReferenceWithDistance> courts = nearestCourtsByCourtPostcodeAndAreaOfLawSearch
                .search(mapitData, postcode, serviceArea.getAreaOfLaw().getName());

            return fallbackProximitySearchIfEmpty(courts, serviceArea, postcode, mapitData);
        }

        return nearestCourtsByPostcodeAndAreaOfLawSearch
            .search(mapitData, postcode, serviceArea.getAreaOfLaw().getName());
    }

    private boolean isRegional(final ServiceArea serviceArea) {
        return serviceArea.getServiceAreaCourts().stream()
            .anyMatch(serviceAreaCourt -> REGIONAL.equals(serviceAreaCourt.getCatchmentType()));
    }

    private List<CourtReferenceWithDistance> fallbackProximityRegionalSearchIfEmpty(final List<CourtReferenceWithDistance> courts,
                                                                                    final ServiceArea serviceArea,
                                                                                    final String postcode,
                                                                                    final MapitData mapitData) {
        if (courts.isEmpty()) {
            return nearestRegionalByAreaOfLawSearch
                .search(mapitData, postcode, serviceArea.getAreaOfLaw().getName());
        }

        return courts;
    }

    private List<CourtReferenceWithDistance> fallbackProximitySearchIfEmpty(final List<CourtReferenceWithDistance> courts,
                                                                            final ServiceArea serviceArea,
                                                                            final String postcode,
                                                                            final MapitData mapitData) {
        if (courts.isEmpty()) {
            return nearestCourtsByPostcodeAndAreaOfLawSearch
                .search(mapitData, postcode, serviceArea.getAreaOfLaw().getName());
        }

        return courts;
    }
}
