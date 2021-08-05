package uk.gov.hmcts.dts.fact.services.admin.list;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.SidebarLocation;
import uk.gov.hmcts.dts.fact.repositories.SidebarLocationRepository;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AdminSidebarLocationService.class)
public class AdminSidebarLocationServiceTest {
    private static final Integer TEST_SIDEBAR_LOCATION_ID1 = 1;
    private static final Integer TEST_SIDEBAR_LOCATION_ID2 = 2;
    private static final String TEST_SIDEBAR_LOCATION1 = "location 1";
    private static final String TEST_SIDEBAR_LOCATION2 = "location 2";
    private static final List<uk.gov.hmcts.dts.fact.entity.SidebarLocation> SIDEBAR_LOCATIONS = Arrays.asList(
        new uk.gov.hmcts.dts.fact.entity.SidebarLocation(TEST_SIDEBAR_LOCATION_ID1, TEST_SIDEBAR_LOCATION1),
        new uk.gov.hmcts.dts.fact.entity.SidebarLocation(TEST_SIDEBAR_LOCATION_ID2, TEST_SIDEBAR_LOCATION2)
    );

    @Autowired
    private AdminSidebarLocationService adminService;

    @MockBean
    private SidebarLocationRepository sidebarLocationRepository;

    @Test
    void shouldReturnAllSidebarLocations() {
        when(sidebarLocationRepository.findAll()).thenReturn(SIDEBAR_LOCATIONS);
        final List<SidebarLocation> results = adminService.getAllSidebarLocations();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(results).hasSize(SIDEBAR_LOCATIONS.size());
        softly.assertThat(results.get(0).getId()).isEqualTo(TEST_SIDEBAR_LOCATION_ID1);
        softly.assertThat(results.get(0).getName()).isEqualTo(TEST_SIDEBAR_LOCATION1);
        softly.assertThat(results.get(1).getId()).isEqualTo(TEST_SIDEBAR_LOCATION_ID2);
        softly.assertThat(results.get(1).getName()).isEqualTo(TEST_SIDEBAR_LOCATION2);
        softly.assertAll();
    }
}
