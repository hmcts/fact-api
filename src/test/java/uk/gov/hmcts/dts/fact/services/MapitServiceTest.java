package uk.gov.hmcts.dts.fact.services;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.mapit.Coordinates;
import uk.gov.hmcts.dts.fact.mapit.MapitClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapitService.class)
class MapitServiceTest {

    @MockBean
    private MapitClient mapitClient;

    @MockBean
    private Logger logger;

    @Autowired
    private MapitService mapitService;

    @Test
    void shouldReturnOptionalOfCoordinatesForValidPostcode() {
        final String postcode = "OX1 1RZ";
        final Coordinates coordinates = new Coordinates(51.7, -1.2);
        when(mapitClient.getCoordinates(postcode)).thenReturn(coordinates);

        final Optional<Coordinates> result = mapitService.getCoordinates(postcode);

        assertThat(result.isPresent()).isTrue();
        assertThat(result).isEqualTo(Optional.of(coordinates));
    }

    @Test
    void shouldReturnOptionalEmptyForUnsupportedPostcode() {
        final String postcode = "JE3 4BA";
        final Coordinates coordinates = new Coordinates(null, null);
        when(mapitClient.getCoordinates(postcode)).thenReturn(coordinates);

        final Optional<Coordinates> result = mapitService.getCoordinates(postcode);

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    void shouldReturnOptionalEmptyIfFeignExceptionIsThrown() {
        final String postcode = "OX1";
        final FeignException feignException = mock(FeignException.class);

        when(mapitClient.getCoordinates(postcode)).thenThrow(feignException);
        when(feignException.status()).thenReturn(400);
        when(feignException.getMessage()).thenReturn("message");

        final Optional<Coordinates> result = mapitService.getCoordinates(postcode);

        assertThat(result.isPresent()).isFalse();
        verify(logger).error("HTTP Status: {} Message: {}", 400, "message", feignException);
    }

    @Test
    void shouldReturnOptionalEmptyIfBlankPostcode() {
        final Optional<Coordinates> result = mapitService.getCoordinates("");

        assertThat(result.isPresent()).isFalse();
        verifyNoInteractions(mapitClient);
    }
}
