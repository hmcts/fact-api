package uk.gov.hmcts.dts.fact.model.admin;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.SidebarLocation;

public class AdditionalLinkTest {
    private static final String TEST_URL = "https://www.additional-link.com";
    private static final String TEST_DESCRIPTION = "description";
    private static final String TEST_DESCRIPTION_CY = "description cy";
    private static final String TEST_TYPE = "type";
    private static final String TEST_SIDEBAR_LOCATION = "location";
    private static final Integer TEST_SIDEBAR_LOCATION_ID = 10;

    @Test
    void testCreation() {
        final SidebarLocation sidebarLocation = new SidebarLocation(TEST_SIDEBAR_LOCATION_ID, TEST_SIDEBAR_LOCATION);
        final uk.gov.hmcts.dts.fact.entity.AdditionalLink additionalLinkEntity = new uk.gov.hmcts.dts.fact.entity.AdditionalLink(TEST_URL, TEST_DESCRIPTION, TEST_DESCRIPTION_CY, TEST_TYPE, sidebarLocation);

        final AdditionalLink result = new AdditionalLink(additionalLinkEntity);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result.getUrl()).isEqualTo(TEST_URL);
        softly.assertThat(result.getUrlDescription()).isEqualTo(TEST_DESCRIPTION);
        softly.assertThat(result.getUrlDescriptionCy()).isEqualTo(TEST_DESCRIPTION_CY);
        softly.assertThat(result.getSidebarLocationId()).isEqualTo(TEST_SIDEBAR_LOCATION_ID);
        softly.assertAll();
    }
}
