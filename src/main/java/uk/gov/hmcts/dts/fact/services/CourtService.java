package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.deprecated.CourtWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class CourtService {

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private MapitService mapitService;

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
            .map(value -> courtRepository
                .findNearest(value.getLat(), value.getLon())
                .stream()
                .limit(10)
                .map(CourtWithDistance::new)
                .collect(toList()))
            .orElse(emptyList());
    }

    public List<CourtWithDistance> getNearestCourtsByPostcodeAndAreaOfLaw(final String postcode, final String areaOfLaw) {
        return mapitService.getCoordinates(postcode)
            .map(value -> courtRepository
                .findNearest(value.getLat(), value.getLon())
                .stream()
                .filter(c -> c.getAreasOfLaw().stream().anyMatch(a -> areaOfLaw.equalsIgnoreCase(a.getName())))
                .limit(10)
                .map(CourtWithDistance::new)
                .collect(toList()))
            .orElse(emptyList());
    }

}
