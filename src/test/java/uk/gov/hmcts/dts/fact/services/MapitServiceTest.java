package uk.gov.hmcts.dts.fact.services;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.mapit.MapitArea;
import uk.gov.hmcts.dts.fact.mapit.MapitClient;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.PostcodeSearchUsageRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapitService.class)
class MapitServiceTest {

    private static final String RESPONSE_MESSAGE = "message";

    @MockitoBean
    private MapitClient mapitClient;

    @MockitoBean
    private PostcodeSearchUsageRepository postcodeSearchUsageRepository;

    @MockitoBean
    private Logger logger;

    @Autowired
    private MapitService mapitService;

    @Test
    void shouldReturnOptionalOfCoordinatesForValidPostcode() {
        final String postcode = "OX1 1RZ";
        final MapitData mapitData = new MapitData(51.7, -1.2, null, null, null);
        when(mapitClient.getMapitData(postcode)).thenReturn(mapitData);

        final Optional<MapitData> result = mapitService.getMapitData(postcode);

        assertThat(result)
            .isPresent()
            .isEqualTo(Optional.of(mapitData));
    }

    @Test
    void shouldReturnOptionalOfCoordinatesForValidPartialPostcode() {
        final String postcode = "OX1";
        final MapitData mapitData = new MapitData(51.7, -1.2, null, null, null);
        when(mapitClient.getMapitDataWithPartial(postcode)).thenReturn(mapitData);

        final Optional<MapitData> result = mapitService.getMapitDataWithPartial(postcode);

        assertThat(result)
            .isPresent()
            .isEqualTo(Optional.of(mapitData));
    }

    @Test
    void shouldReturnOptionalEmptyForUnsupportedPostcode() {
        final String postcode = "JE3 4BA";
        final MapitData mapitData = new MapitData(null, null, null, null, null);
        when(mapitClient.getMapitData(postcode)).thenReturn(mapitData);

        final Optional<MapitData> result = mapitService.getMapitData(postcode);

        assertThat(result).isNotPresent();
    }

    @Test
    void shouldReturnOptionalEmptyForUnsupportedPartialPostcode() {
        final String postcode = "JE3";
        final MapitData mapitData = new MapitData(null, null, null, null, null);
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

    @Test
    void localAuthorityExistsShouldReturnTrueForValidLocalAuthorityNames() {
        when(mapitClient.getMapitDataForLocalAuthorities(any(), any())).thenReturn(
            Map.of("100", new MapitArea("100", "Birmingham City Council", "MTD")));

        assertThat(mapitService.localAuthorityExists("Birmingham City Council")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " " })
    @NullSource
    void localAuthorityExistsShouldReturnFalseForEmptyLocalAuthorityNames(String name) {
        assertThat(mapitService.localAuthorityExists(name)).isFalse();

        // We don't need to call the client to validate the name if the name is
        // null, empty or whitespace
        verifyNoInteractions(mapitClient);
    }

    @Test
    void localAuthorityExistsShouldReturnFalseForInvalidLocalAuthorityName() {
        when(mapitClient.getMapitDataForLocalAuthorities(any(), any())).thenReturn(Collections.emptyMap());
        assertThat(mapitService.localAuthorityExists("Non existent")).isFalse();
    }

    @Test
    void localAuthorityExistsShouldReturnFalseIfFeignExceptionThrown() {
        final FeignException feignException = mock(FeignException.class);

        when(mapitClient.getMapitDataForLocalAuthorities(any(), any())).thenThrow(feignException);
        when(feignException.status()).thenReturn(400);
        when(feignException.getMessage()).thenReturn(RESPONSE_MESSAGE);

        assertThat(mapitService.localAuthorityExists("Test Council")).isFalse();
        verify(logger).warn("Mapit API call (local authority validation) failed. HTTP Status: {} Message: {}", 400, RESPONSE_MESSAGE, feignException);
    }
}
