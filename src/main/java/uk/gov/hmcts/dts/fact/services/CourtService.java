package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.CourtWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.CIVIL;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.FAMILY;

@Service
public class CourtService {

    private static final String LOCAL_AUTHORITY = "local-authority";
    private static final String REGIONAL = "regional";

    private final MapitService mapitService;
    private final CourtRepository courtRepository;
    private final CourtWithDistanceRepository courtWithDistanceRepository;
    private final ServiceAreaRepository serviceAreaRepository;

    @Autowired
    public CourtService(final MapitService mapitService,
                        final CourtRepository courtRepository,
                        final CourtWithDistanceRepository courtWithDistanceRepository,
                        final ServiceAreaRepository serviceAreaRepository) {
        this.mapitService = mapitService;
        this.courtWithDistanceRepository = courtWithDistanceRepository;
        this.courtRepository = courtRepository;
        this.serviceAreaRepository = serviceAreaRepository;
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
        return mapitService.getCoordinates(postcode)
            .map(value -> courtWithDistanceRepository
                .findNearestTen(value.getLat(), value.getLon())
                .stream()
                .map(CourtWithDistance::new)
                .collect(toList()))
            .orElse(emptyList());
    }

    public List<CourtWithDistance> getNearestCourtsByPostcodeAndAreaOfLaw(final String postcode, final String areaOfLaw) {
        return mapitService.getCoordinates(postcode)
            .map(value -> courtWithDistanceRepository
                .findNearestTenByAreaOfLaw(value.getLat(), value.getLon(), areaOfLaw)
                .stream()
                .map(CourtWithDistance::new)
                .collect(toList()))
            .orElse(emptyList());
    }

    public List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> getNearestCourtsByPostcodeAndAreaOfLawSearch(final Double lat, final Double lon, final String areaOfLaw) {
        return courtWithDistanceRepository
            .findNearestTenByAreaOfLaw(lat, lon, areaOfLaw);
    }

    public List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> getNearestCourtsByCourtPostcodeAndAreaOfLawSearch(final Double lat, final Double lon, final String postcode, final String areaOfLaw) {
        return courtWithDistanceRepository
            .findNearestTenByAreaOfLawAndCourtPostcode(lat, lon, areaOfLaw, postcode);
    }

    public List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> findNearestTenByAreaOfLawAndLocalAuthority(final Double lat, final Double lon, final String postcode, final String areaOfLaw) {
        return courtWithDistanceRepository
            .findNearestTenByAreaOfLawAndLocalAuthority(lat, lon, areaOfLaw, postcode);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<CourtReferenceWithDistance> getNearestCourtsByPostcodeSearch(final String postcode, final String serviceAreaSlug) {
        List<uk.gov.hmcts.dts.fact.entity.CourtWithDistance> courts = emptyList();
        final Optional<ServiceArea> serviceAreaOptional = serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug);
        if (serviceAreaOptional.isEmpty() || mapitService.getCoordinates(postcode).isEmpty()) {
            return emptyList();
        }
        final MapitData coordinates = mapitService.getCoordinates(postcode).get();
        ServiceArea serviceArea = serviceAreaOptional.get();
        if (serviceArea.getType().equalsIgnoreCase(FAMILY.toString())
            && LOCAL_AUTHORITY.equals(serviceArea.getCatchmentMethod())
            && serviceArea
            .getServiceAreaCourts()
            .stream()
            .noneMatch(c -> REGIONAL.equals(c.getCatchmentType()))
        ) {
            courts = findNearestTenByAreaOfLawAndLocalAuthority(coordinates.getLat(), coordinates.getLon(), postcode, serviceArea.getAreaOfLaw().getName());
        } else if (serviceArea.getType().equalsIgnoreCase(CIVIL.toString())) {
            courts = getNearestCourtsByCourtPostcodeAndAreaOfLawSearch(coordinates.getLat(), coordinates.getLon(), postcode, serviceArea.getAreaOfLaw().getName());
        }
        if (courts.isEmpty()) {
            courts = getNearestCourtsByPostcodeAndAreaOfLawSearch(coordinates.getLat(), coordinates.getLon(), serviceArea.getAreaOfLaw().getName());
        }
        return courts
            .stream()
            .map(CourtReferenceWithDistance::new)
            .collect(toList());
    }
}
