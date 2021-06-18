package uk.gov.hmcts.dts.fact.mapit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.exception.MapitUsageException;

import java.io.IOException;

@Component
public class MapitHealthIndicator implements HealthIndicator {
    @Autowired
    private MapItHealthService mapItService;

    @Override
    // Mapit health will be marked as 'down' if the 'quota' endpoint can't be reached or the usage limit reached
    public Health health() {
        try {
            return mapItService.isUp()
                ? Health.up().build()
                : Health.down().build();
        } catch (final IOException | MapitUsageException e) {
            return Health.down().withDetail("error", e.getLocalizedMessage()).build();
        }
    }
}
