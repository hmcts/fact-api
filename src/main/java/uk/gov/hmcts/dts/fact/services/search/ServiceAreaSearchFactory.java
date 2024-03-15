package uk.gov.hmcts.dts.fact.services.search;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.util.Action;

import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.serviceAreaTypeFrom;

/**
 * Factory for creating searches for service areas.
 */
@Component
public class ServiceAreaSearchFactory {

    private final DefaultSearch defaultSearch;
    private final FamilySearchFactory familySearchFactory;
    private final CivilSearch civilSearch;

    /**
     * Constructor for the ServiceAreaSearchFactory.
     * @param defaultSearch The default search
     * @param familySearchFactory The family search factory
     * @param civilSearch The civil search
     */
    public ServiceAreaSearchFactory(final DefaultSearch defaultSearch,
                                    final FamilySearchFactory familySearchFactory,
                                    final CivilSearch civilSearch) {
        this.defaultSearch = defaultSearch;
        this.familySearchFactory = familySearchFactory;
        this.civilSearch = civilSearch;
    }

    /**
     * Get the correct search for a given service area, mapit data and action.
     * @param serviceArea The service area
     * @param mapitData The mapit data
     * @param action The action
     * @return The correct search
     */
    public Search getSearchFor(final ServiceArea serviceArea, final MapitData mapitData, final Action action) {

        if (action == Action.NEAREST) {
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
