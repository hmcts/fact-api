package uk.gov.hmcts.dts.fact.services;

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
import uk.gov.hmcts.dts.fact.services.search.ServiceAreaSearchFactory;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class CourtService {

    private final MapitService mapitService;
    private final CourtRepository courtRepository;
    private final CourtWithDistanceRepository courtWithDistanceRepository;
    private final ServiceAreaRepository serviceAreaRepository;
    private final ServiceAreaSearchFactory serviceAreaSearchFactory;

    @Autowired
    public CourtService(final MapitService mapitService,
                        final CourtRepository courtRepository,
                        final CourtWithDistanceRepository courtWithDistanceRepository,
                        final ServiceAreaRepository serviceAreaRepository,
                        final ServiceAreaSearchFactory serviceAreaSearchFactory) {
        this.mapitService = mapitService;
        this.courtWithDistanceRepository = courtWithDistanceRepository;
        this.courtRepository = courtRepository;
        this.serviceAreaRepository = serviceAreaRepository;
        this.serviceAreaSearchFactory = serviceAreaSearchFactory;
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
        return courtRepository
            .queryBy(query)
            .stream()
            .map(CourtReference::new)
            .collect(toList());
    }

    public List<CourtWithDistance> getCourtsByNameOrAddressOrPostcodeOrTown(final String query) {
        return courtRepository
            .queryBy(query)
            .stream()
            .map(CourtWithDistance::new)
            .collect(toList());
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
        return mapitService.getMapitData(postcode)
            .map(value -> courtWithDistanceRepository
                .findNearestTenByAreaOfLaw(value.getLat(), value.getLon(), areaOfLaw)
                .stream()
                .map(CourtWithDistance::new)
                .collect(toList()))
            .orElseThrow(() -> new InvalidPostcodeException(postcode));
    }

    public List<CourtWithDistance> getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority(final String postcode, final String areaOfLaw) {
        final Optional<MapitData> optionalMapitData = mapitService.getMapitData(postcode);
        if (optionalMapitData.isEmpty()) {
            throw new InvalidPostcodeException(postcode);
        }

        final MapitData mapitData = optionalMapitData.get();

        return mapitData.getLocalAuthority()
            .map(localAuthority -> courtWithDistanceRepository
                .findNearestTenByAreaOfLawAndLocalAuthority(mapitData.getLat(), mapitData.getLon(), areaOfLaw, localAuthority)
                .stream()
                .map(CourtWithDistance::new)
                .collect(toList()))
            .orElseThrow(() -> new InvalidPostcodeException(postcode));
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
}
