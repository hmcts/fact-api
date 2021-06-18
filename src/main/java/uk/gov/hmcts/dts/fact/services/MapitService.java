package uk.gov.hmcts.dts.fact.services;

import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.mapit.MapitClient;
import uk.gov.hmcts.dts.fact.mapit.MapitData;

import java.util.Optional;

@Service
public class MapitService {

    private final Logger logger;
    private final MapitClient mapitClient;

    @Autowired
    public MapitService(final Logger logger, final MapitClient mapitClient) {
        this.logger = logger;
        this.mapitClient = mapitClient;
    }

    public Optional<MapitData> getMapitData(final String postcode) {

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

    public Optional<MapitData> getMapitDataWithPartial(final String postcode) {

        if (!StringUtils.isBlank(postcode)) {
            try {
                final MapitData mapitData = mapitClient.getMapitDataWithPartial(postcode);

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
