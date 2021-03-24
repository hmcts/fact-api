package uk.gov.hmcts.dts.fact.mapit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MapitHealthIndicator implements HealthIndicator {
    @Autowired
    private MapItService mapItService;

    @Override
    public Health health() {
        try {
            return mapItService.isUp()
                ? Health.up().build()
                : Health.down().build();
        } catch (final Exception e) {
            return Health.down().withException(e).build();
        }
    }
}
