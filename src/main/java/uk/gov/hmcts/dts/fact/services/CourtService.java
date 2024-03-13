package uk.gov.hmcts.dts.fact.services;

import lombok.extern.slf4j.Slf4j;
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
import uk.gov.hmcts.dts.fact.util.Action;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.services.validation.PostcodeValidator.isFullPostcodeFormat;
import static uk.gov.hmcts.dts.fact.util.Utils.isNorthernIrishPostcode;
import static uk.gov.hmcts.dts.fact.util.Utils.isScottishPostcode;

/**
 * Service to get courts.
 */
@Service
@Slf4j
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

    /**
     * Constructor for the CourtService.
     *
     * @param mapitService               the mapit service
     * @param courtRepository            the repository to get courts from
     * @param proximitySearch            the proximity search
     * @param courtWithDistanceRepository the repository to get courts with distance from
     * @param serviceAreaRepository      the repository to get service areas from
     * @param serviceAreaSearchFactory   the service area search factory
     * @param fallbackProximitySearch    the fallback proximity search
     */
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

    /**
     * Get court by slug (depreciated).
     *
     * @return a court
     */
    public OldCourt getCourtBySlugDeprecated(final String slug) {
        return courtRepository
            .findBySlug(slug)
            .map(OldCourt::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Get court by slug.
     *
     * @return a court
     */
    public Court getCourtBySlug(final String slug) {
        return courtRepository
            .findBySlug(slug)
            .map(Court::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Get all courts by court types.
     *
     * @return the list of courts
     */
    public List<Court> getCourtsByCourtTypes(final List<String> courtTypes) {
        return courtRepository
            .findByCourtTypesSearchIgnoreCaseInAndDisplayedIsTrueOrderByName(courtTypes)
            .stream()
            .map(Court::new)
            .collect(toList());
    }

    /**
     * Get courts by name or address or postcode or town fuzzy match.
     *
     * @return the list of courts
     */
    public List<CourtReference> getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch(final String query) {
        return getCourtsFromRepository(query)
            .stream()
            .map(CourtReference::new)
            .collect(toList());
    }

    /**
     * Get Court by name or address or postcode or town.
     *
     * @return the list of courts
     */
    public List<CourtWithDistance> getCourtsByNameOrAddressOrPostcodeOrTown(final String query, final Boolean includeClosed) {
        return getCourtByQuery(query, includeClosed, CourtWithDistance::new);
    }

    /**
     * Get nearest courts by postcode.
     *
     * @return the list of courts
     */
    public List<CourtWithDistance> getNearestCourtsByPostcode(final String postcode) {
        return mapitService.getMapitData(postcode)
            .map(value -> courtWithDistanceRepository
                .findNearestTen(value.getLat(), value.getLon())
                .stream()
                .map(CourtWithDistance::new)
                .collect(toList()))
            .orElseThrow(() -> new InvalidPostcodeException(postcode));
    }

    /**
     * Get nearest courts by postcode anad area of law.
     *
     * @return the list of courts
     */
    public List<CourtWithDistance> getNearestCourtsByPostcodeAndAreaOfLaw(final String postcode, final String areaOfLaw, final Boolean includeClosed) {
        if (filterResultByPostcode(postcode, areaOfLaw)) {
            return emptyList();
        }

        return mapitService.getMapitData(postcode)
            .map(value -> courtWithDistanceRepository
                .findNearestTenByAreaOfLaw(value.getLat(), value.getLon(), areaOfLaw, includeClosed)
                .stream()
                .filter(getCourtWithDistancePredicate(postcode, areaOfLaw))
                .map(CourtWithDistance::new)
                .collect(toList()))
            .orElseThrow(() -> new InvalidPostcodeException(postcode));
    }

    /**
     * Get nearest courts by postcode, area of law and local authority.
     *
     * @return the list of courts
     */
    public List<CourtWithDistance> getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority(final String postcode, final String areaOfLaw, final Boolean includeClosed) {
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
                .findNearestTenByAreaOfLawAndLocalAuthority(
                    mapitData.getLat(),
                    mapitData.getLon(),
                    areaOfLaw,
                    localAuthority,
                    includeClosed
                ))
            .orElse(emptyList());

        return fallbackProximitySearch.fallbackIfEmpty(courtsWithDistance, areaOfLaw, includeClosed, mapitData)
            .stream()
            .map(CourtWithDistance::new)
            .collect(toList());
    }

    public List<CourtReferenceWithDistance> getNearestCourtReferencesByPostcode(final String postcode) {
        final Optional<MapitData> optionalMapitData = mapitService.getMapitData(postcode);

        if (optionalMapitData.isEmpty()) {
            log.error("No mapit data found for provided postcode: {}", postcode);
            return emptyList(); // Return empty so the frontend logic can be invoked
        }

        List<CourtReferenceWithDistance> courtReferences = convert(proximitySearch.searchWith(optionalMapitData.get()));
        log.debug("Found {} nearest courts for postcode {}: {}",
                  courtReferences.size(), postcode, Arrays.stream(courtReferences.toArray()).toArray()
        );
        return courtReferences;
    }

    public ServiceAreaWithCourtReferencesWithDistance getNearestCourtsByPostcodeSearch(final String postcode,
                                                                                       final String serviceAreaSlug,
                                                                                       final Boolean includeClosed,
                                                                                       final Action action
    ) {

        final Optional<ServiceArea> serviceAreaOptional = serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug);
        final Optional<MapitData> optionalMapitData = mapitService.getMapitData(postcode);

        if (serviceAreaOptional.isEmpty() || optionalMapitData.isEmpty()) {
            return new ServiceAreaWithCourtReferencesWithDistance(serviceAreaSlug);
        }

        final ServiceArea serviceArea = serviceAreaOptional.get();
        final MapitData mapitData = optionalMapitData.get();

        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = serviceAreaSearchFactory
            .getSearchFor(serviceArea, mapitData, action)
            .searchWith(serviceArea, mapitData, postcode, includeClosed);

        return new ServiceAreaWithCourtReferencesWithDistance(serviceArea, convert(courts));
    }

    public ServiceAreaWithCourtReferencesWithDistance getNearestCourtsByPostcodeActionAndAreaOfLawSearch(final String postcode, final String serviceAreaSlug, final Action action, final Boolean includeClosed) {
        final ServiceArea serviceArea = serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug).orElseThrow(() -> new NotFoundException(
            serviceAreaSlug));
        final MapitData mapitData = mapitService.getMapitData(postcode).orElseThrow(() -> new NotFoundException(
            serviceAreaSlug));
        final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = serviceAreaSearchFactory
            .getSearchFor(serviceArea, mapitData, action)
            .searchWith(serviceArea, mapitData, postcode, includeClosed);

        return new ServiceAreaWithCourtReferencesWithDistance(serviceArea, convert(courts));
    }

    public ServiceAreaWithCourtReferencesWithDistance getNearestCourtsByAreaOfLawSinglePointOfEntry(final String postcode, final String serviceArea, final String areaOfLaw, final Action action, final Boolean includeClosed) {
        ServiceAreaWithCourtReferencesWithDistance results = this.getNearestCourtsByPostcodeSearch(
            postcode,
            serviceArea,
            includeClosed,
            action
        );
        if (Objects.isNull(results) || Objects.isNull(results.getCourts())) {
            log.error("Mapit has returned a not found request for postcode: {}", postcode);
            throw new NotFoundException(String.format("Mapit can not find information related to postcode %s", postcode));
        }
        results.setCourts(results.getCourts()
                              .stream()
                              .filter(c -> c.getAreasOfLawSpoe().contains(areaOfLaw))
                              .findFirst()
                              .stream()
                              .collect(toList()));
        return results;
    }

    public List<CourtReference> getCourtsByPrefixAndActiveSearch(String prefix) {
        return courtRepository.findCourtByNameStartingWithIgnoreCaseAndDisplayedOrderByNameAsc(prefix, true)
            .stream()
            .map(CourtReference::new)
            .collect(toList());
    }

    private List<CourtReferenceWithDistance> convert(final List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courtsWithDistance) {
        return courtsWithDistance.stream()
            .map(CourtReferenceWithDistance::new)
            .collect(toList());
    }

    private <T> List<T> getCourtByQuery(final String query, final Boolean includeClosed, final Function<uk.gov.hmcts.dts.fact.entity.Court, T> function) {
        return courtRepository
            .queryBy(query, includeClosed)
            .stream()
            .map(function::apply)
            .collect(toList());
    }

    private List<uk.gov.hmcts.dts.fact.entity.Court> getCourtsFromRepository(final String query) {
        if (query.matches("^\\d+$")) {
            return courtRepository.findCourtByCourtCode(Integer.valueOf(query));
        } else if (isFullPostcodeFormat(query)) {
            return courtRepository.findCourtByFullPostcode(query);
        }

        // For court name, address or town name search, we first search using exact match only (ignore punctuations and casing). If this
        // doesn't return any result, fuzzy match searching will then be attempted.
        List<uk.gov.hmcts.dts.fact.entity.Court> courts = courtRepository.findCourtByNameAddressTownOrPartialPostcodeExactMatch(
            query.replaceAll("[^A-Za-z0-9]+", ""));
        if (courts.isEmpty()) {
            courts = courtRepository.findCourtByNameAddressOrTownFuzzyMatch(query);
        }
        return courts;
    }


    private boolean filterResultByPostcode(final String postcode, final String areaOfLaw) {
        return isScottishPostcode(postcode)
            || isNorthernIrishPostcode(postcode) && !IMMIGRATION_AREA_OF_LAW.equalsIgnoreCase(areaOfLaw);
    }

    private Predicate<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> getCourtWithDistancePredicate(String postcode, String areaOfLaw) {
        // Only allow courts not in Northern Ireland or Glasglow court for Northern Ireland postcode and Immigration area of law
        return c -> !isNorthernIrishPostcode(postcode)
            || IMMIGRATION_AREA_OF_LAW.equalsIgnoreCase(areaOfLaw) && c.getName().contains(GLASGOW_TRIBUNAL_CENTRE);
    }
}
