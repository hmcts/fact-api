package uk.gov.hmcts.dts.fact.services;

import feign.FeignException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.mapit.Coordinates;
import uk.gov.hmcts.dts.fact.mapit.MapitClient;

import java.util.Optional;

@Service
public class MapitService {

    @Autowired
    private Logger logger;

    @Autowired
    private MapitClient mapitClient;

    public Optional<Coordinates> getCoordinates(final String postcode) {

        if (!postcode.isBlank()) {
            try {
                final Coordinates coordinates = mapitClient.getCoordinates(postcode);

                if (coordinates.hasLatAndLonValues()) {
                    return Optional.of(coordinates);
                }
            } catch (final FeignException ex) {
                logger.warn("HTTP Status: {} Message: {}", ex.status(), ex.getMessage(), ex);
            }
        }

        return Optional.empty();
    }

}
