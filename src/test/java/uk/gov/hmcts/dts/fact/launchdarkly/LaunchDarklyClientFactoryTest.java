package uk.gov.hmcts.dts.fact.launchdarkly;

import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


class LaunchDarklyClientFactoryTest {
    private LaunchDarklyClientFactory factory;

    @BeforeEach
    void setUp() {
        factory = new LaunchDarklyClientFactory();
    }

    @Test
    void testCreate() throws IOException {
        try (LDClientInterface client = factory.create("test key", true)) {
            assertThat(client).isNotNull();
        }
    }
}
