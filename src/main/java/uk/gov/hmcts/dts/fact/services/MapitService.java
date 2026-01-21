package uk.gov.hmcts.dts.fact.services;

import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.hmcts.dts.fact.entity.PostcodeSearchUsage;
import uk.gov.hmcts.dts.fact.mapit.MapitClient;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.PostcodeSearchUsageRepository;

import java.util.Optional;

/**
 * Service to get mapit data.
 */
@Service
public class MapitService {

    private final Logger logger;
    private final MapitClient mapitClient;
    private final PostcodeSearchUsageRepository postcodeSearchUsageRepository;

    /**
     * Constructor for the MapitService.
     *
     * @param logger the logger
     * @param mapitClient the client to get mapit data from
     */
    @Autowired
    public MapitService(final Logger logger, final MapitClient mapitClient,
                        final PostcodeSearchUsageRepository postcodeSearchUsageRepository) {
        this.logger = logger;
        this.mapitClient = mapitClient;
        this.postcodeSearchUsageRepository = postcodeSearchUsageRepository;
    }

    /**
     * Get mapit data for a postcode.
     *
     * @param postcode the postcode to get mapit data for
     * @return the mapit data
     */
    public Optional<MapitData> getMapitData(final String postcode) {

        if (!postcode.isBlank()) {
            try {
                final MapitData mapitData = track(mapitClient.getMapitData(postcode));

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
     * Get mapit data for a partial postcode.
     *
     * @param postcode the partial postcode to get mapit data for
     * @return the mapit data
     */
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
     * Check if a local authority exists.
     *
     * @param localAuthorityName the name of the local authority
     * @return true if the local authority exists, false otherwise
     */
    public Boolean localAuthorityExists(final String localAuthorityName) {

        if (StringUtils.isNotBlank(localAuthorityName)) {
            try {
                return mapitClient.getMapitDataForLocalAuthorities(localAuthorityName, "MTD,UTA,LBO,CTY")
                    .values()
                    .stream()
                    .anyMatch(la -> la.getName().equalsIgnoreCase(localAuthorityName));
            } catch (final FeignException ex) {
                logger.warn(
                    "Mapit API call (local authority validation) failed. HTTP Status: {} Message: {}",
                    ex.status(),
                    ex.getMessage(),
                    ex
                );
                return false;
            }
        }
        return false;
    }

    /**
     * Passthrough method that tracks any postcode present in the {@link MapitData} response.
     *
     * @param response {@link MapitData} response
     * @return the {@link MapitData} response
     */
    private MapitData track(MapitData response) {
        try {
            Optional.ofNullable(response.getPostcode())
                .filter(postcode -> !postcode.isBlank())
                .map(PostcodeSearchUsage::fromPostcode)
                .ifPresentOrElse(
                    postcodeSearchUsageRepository::save,
                    () -> logger.info("No valid postcode available in Mapit API response. Unable to track.")
                );
        } catch (final Exception ex) {
            logger.warn("Tracking postcode for Mapit API call failed: ", ex);
        }
        return response;
    }
}
