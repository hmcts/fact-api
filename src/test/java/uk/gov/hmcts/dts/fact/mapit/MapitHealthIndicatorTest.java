package uk.gov.hmcts.dts.fact.mapit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import uk.gov.hmcts.dts.fact.exception.MapitUsageException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MapitHealthIndicatorTest {
    private static final String MAPIT_USAGE_ERROR = "Usage limit reached.";
    @Mock
    private MapItService mapItService;

    @InjectMocks
    private MapitHealthIndicator healthIndicator;

    @Test
    void shouldReturnHealthStatusUpWhenMapitServiceIsRunnning() throws IOException {
        when(mapItService.isUp()).thenReturn(true);
        assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void shouldReturnHealthStatusDownWhenMapitServiceIsNotRunnning() throws IOException {
        when(mapItService.isUp()).thenReturn(false);
        assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void shouldReturnHealthStatusDownWhenMapitServiceThrowsException() throws IOException {
        doThrow(new IOException("IO exception")).when(mapItService).isUp();
        final Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void shouldReturnHealthStatusDownWhenExceptionWhenMapitUsageLimitReached() throws IOException {
        doThrow(new MapitUsageException()).when(mapItService).isUp();
        final Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().get("error")).isEqualTo(MAPIT_USAGE_ERROR);
    }
}
