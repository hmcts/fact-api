package uk.gov.hmcts.dts.fact.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.exception.InvalidPostcodeException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.model.ServiceAreaWithCourtReferencesWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.CourtWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;
import uk.gov.hmcts.dts.fact.services.search.FallbackProximitySearch;
import uk.gov.hmcts.dts.fact.services.search.ProximitySearch;
import uk.gov.hmcts.dts.fact.services.search.ServiceAreaSearchFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.isNorthernIrishPostcode;
import static uk.gov.hmcts.dts.fact.util.Utils.isScottishPostcode;

@Service
public class CourtService {

    private static final String IMMIGRATION_AREA_OF_LAW = "Immigration";
    private static final String GLASGOW_TRIBUNAL_CENTRE = "Glasgow Tribunals Centre";

    private final MapitService mapitService;
    private final CourtRepository courtRepository;
    private final ProximitySearch proximitySearch;
    private final CourtWithDistanceRepository courtWithDistanceRepository;
    private final ServiceAreaRepository serviceAreaRepository;
    private final ServiceAreaSearchFactory serviceAreaSearchFactory;
    private final FallbackProximitySearch fallbackProximitySearch;

    private static final Logger LOGGER = LoggerFactory.getLogger(CourtService.class);

    @Autowired
    public CourtService(final MapitService mapitService,
                        final CourtRepository courtRepository,
                        final ProximitySearch proximitySearch,
                        final CourtWithDistanceRepository courtWithDistanceRepository,
                        final ServiceAreaRepository serviceAreaRepository,
                        final ServiceAreaSearchFactory serviceAreaSearchFactory,
                        final FallbackProximitySearch fallbackProximitySearch) {
        this.mapitService = mapitService;
        this.courtWithDistanceRepository = courtWithDistanceRepository;
        this.proximitySearch = proximitySearch;
        this.courtRepository = courtRepository;
        this.serviceAreaRepository = serviceAreaRepository;
        this.serviceAreaSearchFactory = serviceAreaSearchFactory;
        this.fallbackProximitySearch = fallbackProximitySearch;
    }

    public OldCourt getCourtBySlugDeprecated(final String slug) {
        return courtRepository
            .findBySlug(slug)
            .map(OldCourt::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    public Court getCourtBySlug(final String slug) {
        return courtRepository
            .findBySlug(slug)
            .map(Court::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    public List<CourtReference> getCourtByNameOrAddressOrPostcodeOrTown(final String query) {
        return getCourtByQuery(query, CourtReference::new);
    }

    public List<CourtWithDistance> getCourtsByNameOrAddressOrPostcodeOrTown(final String query) {
        return getCourtByQuery(query, CourtWithDistance::new);
    }

    public List<CourtWithDistance> getNearestCourtsByPostcode(final String postcode) {
        return mapitService.getMapitData(postcode)
            .map(value -> courtWithDistanceRepository
                .findNearestTen(value.getLat(), value.getLon())
                .stream()
                .map(CourtWithDistance::new)
                .collect(toList()))
            .orElseThrow(() -> new InvalidPostcodeException(postcode));
    }

    public List<CourtWithDistance> getNearestCourtsByPostcodeAndAreaOfLaw(final String postcode, final String areaOfLaw) {
        if (filterResultByPostcode(postcode, areaOfLaw)) {
            return emptyList();
        }

        return mapitService.getMapitData(postcode)
            .map(value -> courtWithDistanceRepository
                .findNearestTenByAreaOfLaw(value.getLat(), value.getLon(), areaOfLaw)
                .stream()
                .filter(getCourtWithDistancePredicate(postcode, areaOfLaw))
                .map(CourtWithDistance::new)
                .collect(toList()))
            .orElseThrow(() -> new InvalidPostcodeException(postcode));
    }

    public List<CourtWithDistance> getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority(final String postcode, final String areaOfLaw) {
        if (filterResultByPostcode(postcode, areaOfLaw)) {
            return emptyList();
        }

        final Optional<MapitData> optionalMapitData = mapitService.getMapitData(postcode);
        if (optionalMapitData.isEmpty()) {
            throw new InvalidPostcodeException(postcode);
        }

        final MapitData mapitData = optionalMapitData.get();

        List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courtsWithDistance = mapitData.getLocalAuthority()
            .map(localAuthority -> courtWithDistanceRepository
                .findNearestTenByAreaOfLawAndLocalAuthority(mapitData.getLat(), mapitData.getLon(), areaOfLaw, localAuthority))
            .orElse(emptyList());

        return fallbackProximitySearch.fallbackIfEmpty(courtsWithDistance, areaOfLaw, mapitData)
            .stream()
            .map(CourtWithDistance::new)
            .collect(toList());
    }

    public List<CourtReferenceWithDistance> getNearestCourtsReferencesByPostcode(final String postcode) {
        final Optional<MapitData> optionalMapitData = mapitService.getMapitData(postcode);

        if (optionalMapitData.isEmpty()) {
            LOGGER.error("No mapit data found for provided postcode: {}", postcode);
            throw new NotFoundException("Postcode data not found");
        }

        List<CourtReferenceWithDistance> crwds = convert(proximitySearch.searchWith(optionalMapitData.get()));
        LOGGER.info("Found {} nearest courts for postcode {}: {}",
                    crwds.size(), postcode, Arrays.stream(crwds.toArray()).toArray());
        return crwds;
    }

    public ServiceAreaWithCourtReferencesWithDistance getNearestCourtsByPostcodeSearch(final String postcode, final String serviceAreaSlug) {

        final Optional<ServiceArea> serviceAreaOptional = serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug);
        final Optional<MapitData> optionalMapitData = mapitService.getMapitData(postcode);

        if (serviceAreaOptional.isEmpty() || optionalMapitData.isEmpty()) {
            return new ServiceAreaWithCourtReferencesWithDistance(serviceAreaSlug);
        }

        final ServiceArea serviceArea = serviceAreaOptional.get();
        final MapitData mapitData = optionalMapitData.get();

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = serviceAreaSearchFactory
            .getSearchFor(serviceArea, mapitData)
            .searchWith(serviceArea, mapitData, postcode);

        return new ServiceAreaWithCourtReferencesWithDistance(serviceArea, convert(courts));
    }

    private List<CourtReferenceWithDistance> convert(final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courtsWithDistance) {
        return courtsWithDistance.stream()
            .map(CourtReferenceWithDistance::new)
            .collect(toList());
    }

    private <T> List<T> getCourtByQuery(final String query, final Function<uk.gov.hmcts.dts.fact.entity.Court, T> function) {
        return courtRepository
            .queryBy(query)
            .stream()
            .map(function::apply)
            .collect(toList());
    }

    private boolean filterResultByPostcode(final String postcode, final String areaOfLaw) {
        return isScottishPostcode(postcode)
            || isNorthernIrishPostcode(postcode) && !areaOfLaw.equalsIgnoreCase(IMMIGRATION_AREA_OF_LAW);
    }

    private Predicate<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> getCourtWithDistancePredicate(String postcode, String areaOfLaw) {
        // Only allow courts not in Northern Ireland or Glasglow court for Northern Ireland postcode and Immigration area of law
        return c -> !isNorthernIrishPostcode(postcode)
            || areaOfLaw.equalsIgnoreCase(IMMIGRATION_AREA_OF_LAW) && c.getName().contains(GLASGOW_TRIBUNAL_CENTRE);
    }
}
