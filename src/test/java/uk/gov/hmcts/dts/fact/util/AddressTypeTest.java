package uk.gov.hmcts.dts.fact.util;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.dts.fact.util.AddressType.*;

public class AddressTypeTest {
    @Test
    void testFindByName() {
        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(findByName("Visit us")).isEqualTo(VISIT_US);
        softly.assertThat(findByName("Write to us")).isEqualTo(WRITE_TO_US);
        softly.assertThat(findByName("Visit or contact us")).isEqualTo(VISIT_OR_CONTACT_US);
        softly.assertThatThrownBy(() -> findByName("unknown type"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown address type: unknown type");
        softly.assertAll();
    }

    @Test
    void testIsCourtAddress() {
        assertThat(isCourtAddress("Visit us")).isTrue();
        assertThat(isCourtAddress("Write to us")).isFalse();
    }
}
