package uk.gov.hmcts.dts.fact.mapit;

import com.fasterxml.jackson.databind.JsonNode;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.dts.fact.mapit.MapItHealthService.*;

@ExtendWith(MockitoExtension.class)
public class MapItHealthServiceTest {
    @Mock
    RestTemplate restTemplate;

    @Mock
    ResponseEntity<JsonNode> response;

    @Mock
    JsonNode responseBody;

    @Mock
    JsonNode quota;

    @Mock
    JsonNode limit;

    @Mock
    JsonNode current;

    @InjectMocks
    private MapItHealthService mapItHealthService;

    @Test
    void testMapItReturnsNoBodyAndOkStatus() throws IOException {
        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);
        when(response.getBody()).thenReturn(null);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        assertThat(mapItHealthService.isUp()).isTrue();
    }

    @Test
    void testMapItReturnsNoBodyAndBadRequestStatus() throws IOException {
        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);
        when(response.getBody()).thenReturn(null);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        assertThat(mapItHealthService.isUp()).isFalse();
    }

    @Test
    void testMapItReturnsBodyWithNoQuotaAndOkStatus() throws IOException {
        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);
        when(response.getBody()).thenReturn(responseBody);
        when(responseBody.get(QUOTA)).thenReturn(null);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        assertThat(mapItHealthService.isUp()).isTrue();
    }

    @Test
    void testMapItReturnsBodyWithNoQuotaLimitAndOkStatus() throws IOException {
        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);
        when(response.getBody()).thenReturn(responseBody);
        when(responseBody.get(QUOTA)).thenReturn(quota);
        when(quota.get(LIMIT)).thenReturn(limit);
        when(limit.asInt()).thenReturn(0);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        assertThat(mapItHealthService.isUp()).isTrue();
    }

    @Test
    void testMapItUsageExceptionWithQuotaLimitNotZero() {
        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);
        when(response.getBody()).thenReturn(responseBody);
        when(responseBody.get(QUOTA)).thenReturn(quota);
        when(quota.get(LIMIT)).thenReturn(limit);
        when(limit.asInt()).thenReturn(10);
        assertThatThrownBy(() -> mapItHealthService.isUp())
            .isInstanceOf(MapitUsageException.class);
    }

    @Test
    void testMapItReturnsBodyWithQuotaLimitNotExceededAndOkStatus() throws IOException {
        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);
        when(response.getBody()).thenReturn(responseBody);
        when(responseBody.get(QUOTA)).thenReturn(quota);
        when(quota.get(LIMIT)).thenReturn(limit);
        when(quota.get(CURRENT)).thenReturn(current);
        when(limit.asInt()).thenReturn(3);
        when(current.asInt()).thenReturn(2);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        assertThat(mapItHealthService.isUp()).isTrue();
    }

    @Test
    void testMapItReturnsBodyWithQuotaLimitExceeded() {
        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);
        when(response.getBody()).thenReturn(responseBody);
        when(responseBody.get(QUOTA)).thenReturn(quota);
        when(quota.get(LIMIT)).thenReturn(limit);
        when(quota.get(CURRENT)).thenReturn(current);
        when(limit.asInt()).thenReturn(2);
        when(current.asInt()).thenReturn(3);
        assertThatThrownBy(() -> mapItHealthService.isUp())
            .isInstanceOf(MapitUsageException.class);
    }
}
