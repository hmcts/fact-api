package uk.gov.hmcts.dts.fact.model.admin;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class AdditionalLinkTest {
    private static final String TEST_URL = "https://www.additional-link.com";
    private static final String TEST_DESCRIPTION = "description";
    private static final String TEST_DESCRIPTION_CY = "description cy";
    @Test
    void testCreation() {
        final uk.gov.hmcts.dts.fact.entity.AdditionalLink additionalLinkEntity = new uk.gov.hmcts.dts.fact.entity.AdditionalLink(TEST_URL, TEST_DESCRIPTION, TEST_DESCRIPTION_CY);

        final AdditionalLink result = new AdditionalLink(additionalLinkEntity);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result.getUrl()).isEqualTo(TEST_URL);
        softly.assertThat(result.getDisplayName()).isEqualTo(TEST_DESCRIPTION);
        softly.assertThat(result.getDisplayNameCy()).isEqualTo(TEST_DESCRIPTION_CY);
        softly.assertAll();
    }
}
