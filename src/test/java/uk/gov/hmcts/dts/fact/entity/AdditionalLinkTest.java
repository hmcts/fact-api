package uk.gov.hmcts.dts.fact.entity;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

public class AdditionalLinkTest {
    private static final String TEST_URL = "www.test.com";
    private static final String TEST_DESCRIPTION = "description";
    private static final String TEST_DESCRIPTION_CY = "";

    @Test
    void testCreation() {
        final AdditionalLink additionalLink = new AdditionalLink(1, TEST_URL, TEST_DESCRIPTION, TEST_DESCRIPTION_CY);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(additionalLink.getUrl()).isEqualTo(TEST_URL);
        softly.assertThat(additionalLink.getDescription()).isEqualTo(TEST_DESCRIPTION);
        softly.assertThat(additionalLink.getDescriptionCy()).isEqualTo(TEST_DESCRIPTION_CY);
        softly.assertAll();
    }
}
