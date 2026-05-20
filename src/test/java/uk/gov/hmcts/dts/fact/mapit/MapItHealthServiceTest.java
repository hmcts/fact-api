package uk.gov.hmcts.dts.fact.mapit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.dts.fact.exception.MapitUsageException;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapItHealthServiceTest {
    @Mock
    RestTemplate restTemplate;

    @Mock
    ResponseEntity<String> response;

    @InjectMocks
    private MapItHealthService mapItHealthService;

    @Test
    void testMapItReturnsNoBodyAndOkStatus() throws IOException {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);
        when(response.getBody()).thenReturn(null);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        assertThat(mapItHealthService.isUp()).isTrue();
    }

    @Test
    void testMapItReturnsNoBodyAndBadRequestStatus() throws IOException {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);
        when(response.getBody()).thenReturn(null);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        assertThat(mapItHealthService.isUp()).isFalse();
    }

    @Test
    void testMapItReturnsBodyWithNoQuotaAndOkStatus() throws IOException {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);
        when(response.getBody()).thenReturn("{\"foo\":\"bar\"}");
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        assertThat(mapItHealthService.isUp()).isTrue();
    }

    @Test
    void testMapItReturnsBodyWithNoQuotaLimitAndOkStatus() throws IOException {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);
        when(response.getBody()).thenReturn("{\"quota\":{\"limit\":0,\"current\":0}}");
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        assertThat(mapItHealthService.isUp()).isTrue();
    }

    @Test
    void testMapItReturnsBodyWithQuotaLimitNotExceededAndOkStatus() throws IOException {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);
        when(response.getBody()).thenReturn("{\"quota\":{\"limit\":3,\"current\":2}}");
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        assertThat(mapItHealthService.isUp()).isTrue();
    }

    @Test
    void testMapItReturnsBodyWithQuotaLimitExceeded() {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);
        when(response.getBody()).thenReturn("{\"quota\":{\"limit\":2,\"current\":3}}");
        assertThatThrownBy(() -> mapItHealthService.isUp())
            .isInstanceOf(MapitUsageException.class);
    }
}
