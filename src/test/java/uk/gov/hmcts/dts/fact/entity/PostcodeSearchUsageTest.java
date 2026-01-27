package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PostcodeSearchUsageTest {

    static final Map<String, String> TEST_POSTCODES = Map.of(
        "HA9 6QJ", "HA9 6",
        "SA32 7NP", "SA32 7",
        "KY1 2TE", "KY1 2",
        "IP27 0LF", "IP27 0",
        "DA1 1NE", "DA1 1"
    );

    @Test
    void fromPostcode() {
        for (final String postcode : TEST_POSTCODES.keySet()) {
            PostcodeSearchUsage usage = PostcodeSearchUsage.fromPostcode(postcode);
            assertThat(usage.getFullPostcode()).isNotNull().isEqualTo(postcode);
            assertThat(usage.getCachePostcode()).isNotNull().isEqualTo(TEST_POSTCODES.get(postcode));
        }
    }
}
