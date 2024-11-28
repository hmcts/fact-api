package uk.gov.hmcts.dts.fact.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.Application;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Application.class)
public class RateLimiterConfigurationTest {

    @Autowired
    RateLimiterRegistry rateLimiterRegistry;

    @Test
    void shouldHaveRateLimitingProperties() {
        System.out.println(rateLimiterRegistry.rateLimiter("default"));
        RateLimiter defaultRateLimiter = rateLimiterRegistry.rateLimiter("default");

        assertThat(defaultRateLimiter).isNotNull();

        assertThat(defaultRateLimiter.getRateLimiterConfig().getLimitForPeriod()).isEqualTo(15);
        assertThat(defaultRateLimiter.getRateLimiterConfig().getLimitRefreshPeriod())
            .isEqualTo(Duration.ofSeconds(1));
        assertThat(defaultRateLimiter.getRateLimiterConfig().getTimeoutDuration())
            .isEqualTo(Duration.ofSeconds(1));
    }

}
