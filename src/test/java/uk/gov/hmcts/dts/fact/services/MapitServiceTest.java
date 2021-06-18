package uk.gov.hmcts.dts.fact.services;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.mapit.MapitClient;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.services.validation.PostcodeValidator;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapitService.class)
class MapitServiceTest {

    private static final String RESPONSE_MESSAGE = "message";

    @MockBean
    private MapitClient mapitClient;

    @MockBean
    private Logger logger;

    @Autowired
    private MapitService mapitService;

    @Test
    void shouldReturnOptionalOfCoordinatesForValidPostcode() {
        final String postcode = "OX1 1RZ";
        final MapitData mapitData = new MapitData(51.7, -1.2, null, null);
        when(mapitClient.getMapitData(postcode)).thenReturn(mapitData);

        final Optional<MapitData> result = mapitService.getMapitData(postcode);

        assertThat(result)
            .isPresent()
            .isEqualTo(Optional.of(mapitData));
    }

    @Test
    void shouldReturnOptionalOfCoordinatesForValidPartialPostcode() {
        final String postcode = "OX1";
        final MapitData mapitData = new MapitData(51.7, -1.2, null, null);
        when(mapitClient.getMapitDataWithPartial(postcode)).thenReturn(mapitData);

        final Optional<MapitData> result = mapitService.getMapitDataWithPartial(postcode);

        assertThat(result)
            .isPresent()
            .isEqualTo(Optional.of(mapitData));
    }

    @Test
    void shouldReturnOptionalEmptyForUnsupportedPostcode() {
        final String postcode = "JE3 4BA";
        final MapitData mapitData = new MapitData(null, null, null, null);
        when(mapitClient.getMapitData(postcode)).thenReturn(mapitData);

        final Optional<MapitData> result = mapitService.getMapitData(postcode);

        assertThat(result).isNotPresent();
    }

    @Test
    void shouldReturnOptionalEmptyForUnsupportedPartialPostcode() {
        final String postcode = "JE3";
        final MapitData mapitData = new MapitData(null, null, null, null);
        when(mapitClient.getMapitDataWithPartial(postcode)).thenReturn(mapitData);

        final Optional<MapitData> result = mapitService.getMapitDataWithPartial(postcode);

        assertThat(result).isNotPresent();
    }

    @Test
    void shouldReturnOptionalEmptyIfFeignExceptionIsThrown() {
        final String postcode = "OX1";
        final FeignException feignException = mock(FeignException.class);

        when(mapitClient.getMapitData(postcode)).thenThrow(feignException);
        when(feignException.status()).thenReturn(400);
        when(feignException.getMessage()).thenReturn(RESPONSE_MESSAGE);

        final Optional<MapitData> result = mapitService.getMapitData(postcode);

        assertThat(result).isNotPresent();
        verify(logger).warn("HTTP Status: {} Message: {}", 400, RESPONSE_MESSAGE, feignException);
    }

    @Test
    void shouldReturnOptionalEmptyIfFeignExceptionIsThrownForPartialPostcode() {
        final String postcode = "OX1 1B";
        final FeignException feignException = mock(FeignException.class);

        when(mapitClient.getMapitDataWithPartial(postcode)).thenThrow(feignException);
        when(feignException.status()).thenReturn(400);
        when(feignException.getMessage()).thenReturn(RESPONSE_MESSAGE);

        final Optional<MapitData> result = mapitService.getMapitDataWithPartial(postcode);

        assertThat(result).isNotPresent();
        verify(logger).warn("HTTP Status: {} Message: {}", 400, RESPONSE_MESSAGE, feignException);
    }

    @Test
    void shouldReturnOptionalEmptyIfBlankPostcode() {
        final Optional<MapitData> result = mapitService.getMapitData("");

        assertThat(result).isNotPresent();
        verifyNoInteractions(mapitClient);
    }

    @Test
    void shouldReturnOptionalEmptyIfBlankPostcodeForPartial() {
        final Optional<MapitData> result = mapitService.getMapitDataWithPartial("");

        assertThat(result).isNotPresent();
        verifyNoInteractions(mapitClient);
    }
}
