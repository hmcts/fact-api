package uk.gov.hmcts.dts.fact.model.admin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.ApplicationUpdate;

import static org.assertj.core.api.Assertions.assertThat;


public class ApplicationUpdateTest {
    private static final String TYPE = "type";
    private static final String TYPE_CY = "type cy";
    private static final String EMAIL = "email@test.com";
    private static final String EXTERNAL_LINK = "link";
    private static final String EXTERNAL_LINK_DESCRIPTION = "Description";
    private static final String EXTERNAL_LINK_DESCRIPTION_CY = "Description cy";
    private static final ApplicationUpdate APPLICATION_UPDATE = new ApplicationUpdate();

    @BeforeAll
    static void setUp() {
        APPLICATION_UPDATE.setType(TYPE);
        APPLICATION_UPDATE.setTypeCy(TYPE_CY);
        APPLICATION_UPDATE.setEmail(EMAIL);
        APPLICATION_UPDATE.setExternalLink(EXTERNAL_LINK);
        APPLICATION_UPDATE.setExternalLinkDescription(EXTERNAL_LINK_DESCRIPTION);
        APPLICATION_UPDATE.setExternalLinkDescriptionCy(EXTERNAL_LINK_DESCRIPTION_CY);
    }

    @Test
    void testCreationOfApplicationUpdate() {
        uk.gov.hmcts.dts.fact.model.admin.ApplicationUpdate applicationUpdateModel = new uk.gov.hmcts.dts.fact.model.admin.ApplicationUpdate(APPLICATION_UPDATE);

        assertThat(applicationUpdateModel.getType()).isEqualTo(TYPE);
        assertThat(applicationUpdateModel.getTypeCy()).isEqualTo(TYPE_CY);
        assertThat(applicationUpdateModel.getEmail()).isEqualTo(EMAIL);
        assertThat(applicationUpdateModel.getExternalLink()).isEqualTo(EXTERNAL_LINK);
        assertThat(applicationUpdateModel.getExternalLinkDescription()).isEqualTo(EXTERNAL_LINK_DESCRIPTION);
        assertThat(applicationUpdateModel.getExternalLinkDescriptionCy()).isEqualTo(EXTERNAL_LINK_DESCRIPTION_CY);
    }
}
