package uk.gov.hmcts.dts.fact.entity;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

public class AdditionalLinkTest {
    private static final String TEST_URL = "www.test.com";
    private static final String TEST_DESCRIPTION = "description";
    private static final String TEST_DESCRIPTION_CY = "";
    private static final String TEST_SIDEBAR_LOCATION_NAME = "This location handles";
    private static final SidebarLocation TEST_SIDEBAR_LOCATION = new SidebarLocation(2, TEST_SIDEBAR_LOCATION_NAME);

    @Test
    void testCreation() {
        final AdditionalLink additionalLink = new AdditionalLink(1, TEST_URL, TEST_DESCRIPTION, TEST_DESCRIPTION_CY, TEST_SIDEBAR_LOCATION);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(additionalLink.getUrl()).isEqualTo(TEST_URL);
        softly.assertThat(additionalLink.getDescription()).isEqualTo(TEST_DESCRIPTION);
        softly.assertThat(additionalLink.getDescriptionCy()).isEqualTo(TEST_DESCRIPTION_CY);
        softly.assertThat(additionalLink.getLocation().getName()).isEqualTo(TEST_SIDEBAR_LOCATION_NAME);
        softly.assertAll();
    }
}
