package uk.gov.hmcts.dts.fact.model.admin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SidebarLocationTest {
    private static final Integer TEST_ID = 5;
    private static final String TEST_LOCATION = "sidebar";

    @Test
    void testCreation() {
        final uk.gov.hmcts.dts.fact.entity.SidebarLocation sidebarLocationEntity = new uk.gov.hmcts.dts.fact.entity.SidebarLocation(TEST_ID, TEST_LOCATION);
        final SidebarLocation result = new SidebarLocation(sidebarLocationEntity);
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getName()).isEqualTo(TEST_LOCATION);
    }
}
