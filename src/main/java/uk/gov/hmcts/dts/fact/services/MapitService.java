package uk.gov.hmcts.dts.fact.services;

import feign.FeignException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.mapit.MapitClient;
import uk.gov.hmcts.dts.fact.mapit.MapitData;

import java.util.Optional;

@Service
public class MapitService {

    @Autowired
    private Logger logger;

    @Autowired
    private MapitClient mapitClient;

    public Optional<MapitData> getCoordinates(final String postcode) {

        if (!postcode.isBlank()) {
            try {
                final MapitData mapitData = mapitClient.getMapitData(postcode);

                if (mapitData.hasLatAndLonValues()) {
                    return Optional.of(mapitData);
                }
            } catch (final FeignException ex) {
                logger.warn("HTTP Status: {} Message: {}", ex.status(), ex.getMessage(), ex);
            }
        }

        return Optional.empty();
    }

}
