package uk.gov.hmcts.dts.fact.util;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressTypeTest {
    @Test
    void testFindByName() {
        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(AddressType.findByName("Visit us")).isEqualTo(AddressType.VISIT_US);
        softly.assertThat(AddressType.findByName("Write to us")).isEqualTo(AddressType.WRITE_TO_US);
        softly.assertThat(AddressType.findByName("Visit or contact us")).isEqualTo(AddressType.VISIT_OR_CONTACT_US);
        softly.assertThatThrownBy(() -> AddressType.findByName("unknown type"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown address type: unknown type");
        softly.assertAll();
    }

    @Test
    void testIsCourtAddress() {
        assertThat(AddressType.isCourtAddress("Visit us")).isTrue();
        assertThat(AddressType.isCourtAddress("Write to us")).isFalse();
    }
}
