package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;

import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.CIVIL;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.FAMILY;

@Component
public class PostcodeSearchForServiceAreaFactory {

    private static final String LOCAL_AUTHORITY = "local-authority";
    private static final String REGIONAL = "regional";

    private final NearestRegionalByAreaOfLawAndLocalAuthoritySearch nearestRegionalByAreaOfLawAndLocalAuthoritySearch;
    private final NearestTenByAreaOfLawAndLocalAuthoritySearch nearestTenByAreaOfLawAndLocalAuthoritySearch;
    private final NearestCourtsByCourtPostcodeAndAreaOfLawSearch nearestCourtsByCourtPostcodeAndAreaOfLawSearch;
    private final NearestCourtsByPostcodeAndAreaOfLawSearch nearestCourtsByPostcodeAndAreaOfLawSearch;

    public PostcodeSearchForServiceAreaFactory(final NearestRegionalByAreaOfLawAndLocalAuthoritySearch nearestRegionalByAreaOfLawAndLocalAuthoritySearch,
                                               final NearestTenByAreaOfLawAndLocalAuthoritySearch nearestTenByAreaOfLawAndLocalAuthoritySearch,
                                               final NearestCourtsByCourtPostcodeAndAreaOfLawSearch nearestCourtsByCourtPostcodeAndAreaOfLawSearch,
                                               final NearestCourtsByPostcodeAndAreaOfLawSearch nearestCourtsByPostcodeAndAreaOfLawSearch) {
        this.nearestRegionalByAreaOfLawAndLocalAuthoritySearch = nearestRegionalByAreaOfLawAndLocalAuthoritySearch;
        this.nearestTenByAreaOfLawAndLocalAuthoritySearch = nearestTenByAreaOfLawAndLocalAuthoritySearch;
        this.nearestCourtsByCourtPostcodeAndAreaOfLawSearch = nearestCourtsByCourtPostcodeAndAreaOfLawSearch;
        this.nearestCourtsByPostcodeAndAreaOfLawSearch = nearestCourtsByPostcodeAndAreaOfLawSearch;
    }

    public Search getSearchFor(final ServiceArea serviceArea, final MapitData mapitData) {

        final String type = serviceArea.getType();

        if (FAMILY.isEqualTo(type)) {

            if (LOCAL_AUTHORITY.equals(serviceArea.getCatchmentMethod())
                && mapitData.getLocalAuthority().isPresent()) {

                if (isRegional(serviceArea)) {
                    return nearestRegionalByAreaOfLawAndLocalAuthoritySearch;
                }

                return nearestTenByAreaOfLawAndLocalAuthoritySearch;
            }

        } else if (CIVIL.isEqualTo(type)) {
            return nearestCourtsByCourtPostcodeAndAreaOfLawSearch;
        }

        return nearestCourtsByPostcodeAndAreaOfLawSearch;
    }

    private boolean isRegional(final ServiceArea serviceArea) {
        return serviceArea.getServiceAreaCourts().stream()
            .anyMatch(serviceAreaCourt -> REGIONAL.equals(serviceAreaCourt.getCatchmentType()));
    }
}
