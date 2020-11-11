package uk.gov.hmcts.dts.fact.mapit;

import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class MapitFallbackClientFactory implements FallbackFactory<MapitClient> {

    @Override
    public MapitClient create(final Throwable cause) {

        return new MapitClient() {

            final Logger logger = LoggerFactory.getLogger(MapitClient.class);
            final String httpStatus = cause instanceof FeignException ? Integer.toString(((FeignException) cause).status()) : "";
            final String message = cause.getMessage();

            @Override
            public Coordinates getCoordinates(final String postcode) {
                logger.error("HTTP Status: {} Message: {}", httpStatus, message);
                return new Coordinates(null, null);
            }
        };
    }
}
