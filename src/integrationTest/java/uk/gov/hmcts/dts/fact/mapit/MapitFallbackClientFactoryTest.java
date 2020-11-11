package uk.gov.hmcts.dts.fact.mapit;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MapitFallbackClientFactoryTest {

    @MockBean
    private Logger logger;

    @Autowired
    private MapitFallbackClientFactory mapitFallbackClientFactory;

    @Test
    void shouldCreateMapitFallbackClientForFeignException() {

        final String message = "Message";
        final int statusNotFound = 404;

        final FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(statusNotFound);
        when(feignException.getMessage()).thenReturn(message);

        final MapitClient mapitClient = mapitFallbackClientFactory.create(feignException);
        final Coordinates coordinates = mapitClient.getCoordinates("OX1");

        assertThat(coordinates.isPresent()).isEqualTo(false);
        verify(logger).error("HTTP Status: {} Message: {}", String.valueOf(statusNotFound), message);
    }

    @Test
    void shouldCreateMapitFallbackClientForException() {

        final String message = "Message";
        final Exception exception = mock(Exception.class);
        when(exception.getMessage()).thenReturn(message);

        final MapitClient mapitClient = mapitFallbackClientFactory.create(exception);
        final Coordinates coordinates = mapitClient.getCoordinates("OX1");

        assertThat(coordinates.isPresent()).isEqualTo(false);
        verify(logger).error("HTTP Status: {} Message: {}", "", message);
    }
}
