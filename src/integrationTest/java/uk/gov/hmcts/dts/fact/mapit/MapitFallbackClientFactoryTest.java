package uk.gov.hmcts.dts.fact.mapit;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class MapitFallbackClientFactoryTest {

    @Autowired
    private MapitFallbackClientFactory mapitFallbackClientFactory;

    @Test
    void shouldCreateMapitFallbackClient() {

        final FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(404);
        when(feignException.getMessage()).thenReturn("Message");

        final MapitClient mapitClient = mapitFallbackClientFactory.create(feignException);
        final Coordinates coordinates = mapitClient.getCoordinates("OX1");

        assertThat(coordinates.isPresent()).isEqualTo(false);
    }
}
