package uk.gov.hmcts.dts.fact.mapit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapitHealthIndicatorTest {
    @Mock
    private MapItService mapItService;

    @InjectMocks
    private MapitHealthIndicator healthIndicator;

    @Test
    void shouldReturnHealthStatusUpWhenMapitServiceIsRunnning() {
        when(mapItService.isUp()).thenReturn(true);
        assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void shouldReturnHealthStatusDownWhenMapitServiceIsNotRunnning() {
        when(mapItService.isUp()).thenReturn(false);
        assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void shouldReturnHealthStatusDownWhenExceptionIsThrownWhenAccessingMapitService() {
        doThrow(RuntimeException.class).when(mapItService).isUp();
        assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
    }
}
