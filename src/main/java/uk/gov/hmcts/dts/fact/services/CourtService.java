package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.SlugNotFoundException;
import uk.gov.hmcts.dts.fact.model.Court2;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.services.model.Coordinates;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class CourtService {

    @Autowired
    CourtRepository courtRepository;

    @Autowired
    MapitClient mapitClient;

    public uk.gov.hmcts.dts.fact.model.Court getCourtBySlug(String slug) {
        return courtRepository
            .findBySlug(slug)
            .map(uk.gov.hmcts.dts.fact.model.Court::new)
            .orElseThrow(() -> new SlugNotFoundException(slug));
    }

    public List<CourtReference> getCourtByNameOrAddressOrPostcodeOrTown(String query) {
        return courtRepository
            .queryBy(query)
            .stream()
            .map(court -> new CourtReference(court.getName(), court.getSlug()))
            .collect(toList());
    }

    public List<Court2> getNearestCourtsByPostcode(String postcode) {
        Coordinates coordinates = mapitClient.getCoordinates(postcode);

        return courtRepository
            .findNearest(coordinates.getLat(), coordinates.getLon())
            .stream()
            .limit(10)
            .map(Court2::new)
            .collect(toList());
    }

    public List<Court2> getNearestCourtsByPostcodeAndAreaOfLaw(String postcode, String areaOfLaw) {
        Coordinates coordinates = mapitClient.getCoordinates(postcode);

        return courtRepository
            .findNearest(coordinates.getLat(), coordinates.getLon())
            .stream()
            .filter(c -> c.getAreasOfLaw().stream().anyMatch(a -> areaOfLaw.equalsIgnoreCase(a.getName())))
            .limit(10)
            .map(Court2::new)
            .collect(toList());
    }
}
