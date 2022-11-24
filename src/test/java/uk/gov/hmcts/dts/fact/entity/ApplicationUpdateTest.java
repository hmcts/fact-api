package uk.gov.hmcts.dts.fact.entity;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class ApplicationUpdateTest {
    private static final String TEST_TYPE = "English type";
    private static final String TEST_TYPE_CY = "Welsh type";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_EXTERNAL_LINK = "www.test.com";
    private static final String TEST_EXTERNAL_LINK_DESC = "English link description";
    private static final String TEST_EXTERNAL_LINK_DESC_CY = "Welsh link description";

    @Test
    void testCreation() {
        final ApplicationUpdate applicationUpdate = new ApplicationUpdate(1, TEST_TYPE, TEST_TYPE_CY, TEST_EMAIL,
                                                                          TEST_EXTERNAL_LINK, TEST_EXTERNAL_LINK_DESC, TEST_EXTERNAL_LINK_DESC_CY);
        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(applicationUpdate.getType()).isEqualTo(TEST_TYPE);
        softly.assertThat(applicationUpdate.getTypeCy()).isEqualTo(TEST_TYPE_CY);
        softly.assertThat(applicationUpdate.getEmail()).isEqualTo(TEST_EMAIL);
        softly.assertThat(applicationUpdate.getExternalLink()).isEqualTo(TEST_EXTERNAL_LINK);
        softly.assertThat(applicationUpdate.getExternalLinkDescription()).isEqualTo(TEST_EXTERNAL_LINK_DESC);
        softly.assertThat(applicationUpdate.getExternalLinkDescriptionCy()).isEqualTo(TEST_EXTERNAL_LINK_DESC_CY);
        softly.assertAll();
    }
}
