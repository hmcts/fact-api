package uk.gov.hmcts.dts.fact.services;

import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.mapit.MapitClient;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.services.validation.PostcodeValidator;

import java.util.Arrays;
import java.util.Optional;

@Service
public class MapitService {

    private final Logger logger;
    private final MapitClient mapitClient;
    private final PostcodeValidator mapitPostcodeValidator;

    @Autowired
    public MapitService(final Logger logger, final MapitClient mapitClient,
                        final PostcodeValidator mapitPostcodeValidator) {
        this.logger = logger;
        this.mapitClient = mapitClient;
        this.mapitPostcodeValidator = mapitPostcodeValidator;
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

    /**
     * Accepts an array of strings and checks for each if mapit data exists.
     * @param postcodes An array of strings which are postcodes
     * @return An array of strings which indicate which postcodes have failed to return geographical information
     */
    public String[] validatePostcodes(String... postcodes) {
        return Arrays.stream(postcodes)
            .filter(postcode -> !mapitPostcodeValidator.postcodeDataExists(postcode.replaceAll("\\s+","")))
            .toArray(String[]::new);
    }
}
