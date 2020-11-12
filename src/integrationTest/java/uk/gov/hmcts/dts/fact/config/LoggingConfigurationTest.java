package uk.gov.hmcts.dts.fact.config;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoggingConfigurationTest {

    @Autowired
    private Logger logger;

    @Test
    void shouldAutowireLogger() {
        assertThat(logger.getName()).isEqualTo("uk.gov.hmcts.dts.fact.config.LoggingConfigurationTest");
    }
}
