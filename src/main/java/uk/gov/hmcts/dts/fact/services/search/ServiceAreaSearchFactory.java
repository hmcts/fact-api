package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;

import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.serviceAreaTypeFrom;

@Component
public class ServiceAreaSearchFactory {

    private final DefaultSearch defaultSearch;
    private final FamilySearchFactory familySearchFactory;
    private final CivilSearch civilSearch;

    public ServiceAreaSearchFactory(final DefaultSearch defaultSearch,
                                    final FamilySearchFactory familySearchFactory,
                                    final CivilSearch civilSearch) {
        this.defaultSearch = defaultSearch;
        this.familySearchFactory = familySearchFactory;
        this.civilSearch = civilSearch;
    }

    public Search getSearchFor(final ServiceArea serviceArea, final MapitData mapitData) {

        switch (serviceAreaTypeFrom(serviceArea.getType())) {
            case FAMILY:
                return familySearchFactory.getSearchFor(serviceArea, mapitData);
            case CIVIL:
                return civilSearch;
            default:
                return defaultSearch;
        }

    }

    public Search getSearchForNearest(final ServiceArea serviceArea, final MapitData mapitData, final String action) {

        if (action.equals("nearest")) {
            return defaultSearch;
        } else {
            switch (serviceAreaTypeFrom(serviceArea.getType())) {
                case FAMILY:
                    return familySearchFactory.getSearchFor(serviceArea, mapitData);
                case CIVIL:
                    return civilSearch;
                default:
                    return defaultSearch;
            }
        }

    }
}
