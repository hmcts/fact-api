package uk.gov.hmcts.dts.fact.model.admin;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

public class ApplicationUpdateTest {
    private static final String TEST_TYPE = "English type";
    private static final String TEST_TYPE_CY = "Welsh type";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_EXTERNAL_LINK = "www.test.com";
    private static final String TEST_EXTERNAL_LINK_DESC = "English link description";
    private static final String TEST_EXTERNAL_LINK_DESC_CY = "Welsh link description";

    @Test
    void testCreation() {
        final uk.gov.hmcts.dts.fact.entity.ApplicationUpdate applicationUpdateEntity =
            new uk.gov.hmcts.dts.fact.entity.ApplicationUpdate(TEST_TYPE, TEST_TYPE_CY, TEST_EMAIL, TEST_EXTERNAL_LINK,
                                                               TEST_EXTERNAL_LINK_DESC, TEST_EXTERNAL_LINK_DESC_CY);

        final ApplicationUpdate result = new ApplicationUpdate(applicationUpdateEntity);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result.getType()).isEqualTo(TEST_TYPE);
        softly.assertThat(result.getTypeCy()).isEqualTo(TEST_TYPE_CY);
        softly.assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
        softly.assertThat(result.getExternalLink()).isEqualTo(TEST_EXTERNAL_LINK);
        softly.assertThat(result.getExternalLinkDescription()).isEqualTo(TEST_EXTERNAL_LINK_DESC);
        softly.assertThat(result.getExternalLinkDescriptionCy()).isEqualTo(TEST_EXTERNAL_LINK_DESC_CY);
        softly.assertAll();
    }
}
