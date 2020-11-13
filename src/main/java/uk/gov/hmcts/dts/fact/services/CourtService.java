package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.model.ServiceAreaType;
import uk.gov.hmcts.dts.fact.model.deprecated.CourtWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class CourtService {

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @Autowired
    private MapitService mapitService;

    @Autowired
    private ServiceAreaService serviceAreaService;

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

    public List<CourtReferenceWithDistance> getNearestCourtsByPostcodeSearch(final String postcode, final String serviceAreaSlug) {
        List<CourtReferenceWithDistance> courts = emptyList();
        ServiceArea serviceArea = serviceAreaService.getServiceArea(serviceAreaSlug);
        if (serviceArea.getServiceAreaType().equalsIgnoreCase(ServiceAreaType.FAMILY.toString())) {
            //TODO
        } else if (serviceArea.getServiceAreaType().equalsIgnoreCase(ServiceAreaType.CIVIL.toString())) {
            courts = getNearestCourtsByCourtPostcodeAndAreaOfLawSearch(postcode, serviceArea.getAreaOfLawName());
        }

        if (courts.isEmpty()) {
            courts = getNearestCourtsByPostcodeAndAreaOfLawSearch(postcode, serviceArea.getAreaOfLawName());
        }
        return courts;
    }
}
