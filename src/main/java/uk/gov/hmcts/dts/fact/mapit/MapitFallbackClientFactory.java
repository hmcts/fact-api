package uk.gov.hmcts.dts.fact.mapit;

import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class MapitFallbackClientFactory implements FallbackFactory<MapitClient> {

    @Autowired
    private Logger logger;

    @Override
    public MapitClient create(final Throwable cause) {

        return new MapitClient() {

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
