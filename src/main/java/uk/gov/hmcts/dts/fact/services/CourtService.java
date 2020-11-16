package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
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

    public List<CourtReferenceWithDistance> getNearestCourtsByPostcodeAndAreaOfLawSearch(final String postcode, final String areaOfLaw) {
        return mapitService.getCoordinates(postcode)
            .map(value -> courtWithDistanceRepository
                .findNearestTenByAreaOfLaw(value.getLat(), value.getLon(), areaOfLaw)
                .stream()
                .map(CourtReferenceWithDistance::new)
                .collect(toList()))
            .orElse(emptyList());
    }

    public List<CourtReferenceWithDistance> getNearestCourtsByCourtPostcodeAndAreaOfLawSearch(final String postcode, final String areaOfLaw) {
        return mapitService.getCoordinates(postcode)
            .map(value -> courtWithDistanceRepository
                .findNearestTenByAreaOfLawAndCourtPostcode(value.getLat(), value.getLon(), areaOfLaw, postcode)
                .stream()
                .map(CourtReferenceWithDistance::new)
                .collect(toList()))
            .orElse(emptyList());
    }

    public List<CourtReferenceWithDistance> findNearestTenByAreaOfLawAndLocalAuthority(final String postcode, final String areaOfLaw) {
        return mapitService.getCoordinates(postcode)
            .map(value -> courtWithDistanceRepository
                .findNearestTenByAreaOfLawAndLocalAuthority(value.getLat(), value.getLon(), areaOfLaw, postcode)
                .stream()
                .map(CourtReferenceWithDistance::new)
                .collect(toList()))
            .orElse(emptyList());
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<CourtReferenceWithDistance> getNearestCourtsByPostcodeSearch(final String postcode, final String serviceAreaSlug) {
        List<CourtReferenceWithDistance> courts = emptyList();
        final Optional<ServiceArea> serviceAreaOptional = serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug);
        if (serviceAreaOptional.isEmpty()) {
            return courts;
        }

        ServiceArea serviceArea = serviceAreaOptional.get();
        if (serviceArea.getType().equalsIgnoreCase(FAMILY.toString())
            && LOCAL_AUTHORITY.equals(serviceArea.getCatchmentMethod())
            && serviceArea
            .getServiceAreaCourts()
            .stream()
            .noneMatch(c -> REGIONAL.equals(c.getCatchmentType()))
        ) {

            // no regional service centres
            courts = findNearestTenByAreaOfLawAndLocalAuthority(postcode, serviceArea.getAreaOfLaw().getName());
        } else if (serviceArea.getType().equalsIgnoreCase(CIVIL.toString())) {
            courts = getNearestCourtsByCourtPostcodeAndAreaOfLawSearch(postcode, serviceArea.getAreaOfLaw().getName());
        }
        if (courts.isEmpty()) {
            courts = getNearestCourtsByPostcodeAndAreaOfLawSearch(postcode, serviceArea.getAreaOfLaw().getName());
        }
        return courts;
    }
}
