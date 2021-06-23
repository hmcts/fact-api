package uk.gov.hmcts.dts.fact.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PostcodeAreaTest {
    @ParameterizedTest
    @ValueSource(strings = {
        "M", "SS", "YO", "n", "Sw"
    })
    void testValidPostcodeAreas(final String value) {
        assertThat(PostcodeArea.isValidArea(value)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "MM", "z", "YOS", "12", "M1", "SW1A 1AA", "", " "
    })
    void testInvalidPostcodeAreas(final String value) {
        assertThat(PostcodeArea.isValidArea(value)).isFalse();
    }
}
