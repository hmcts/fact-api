package uk.gov.hmcts.dts.fact.launchdarkly;

import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class LaunchDarklyClientFactoryTest {
    private LaunchDarklyClientFactory factory;

    @BeforeEach
    void setUp() {
        factory = new LaunchDarklyClientFactory();
    }

    @Test
    void testCreate() {
        LDClientInterface client = factory.create("test key", true);
        //TODO: Fix PMD violation where client isn't closed.

        assertThat(client).isNotEqualTo(null);
    }
}
