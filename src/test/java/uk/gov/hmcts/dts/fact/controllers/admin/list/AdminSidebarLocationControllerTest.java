package uk.gov.hmcts.dts.fact.controllers.admin.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.model.admin.SidebarLocation;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminSidebarLocationService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminSidebarLocationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminSidebarLocationControllerTest {
    private static final String PATH = "/admin/sidebarLocations";
    private static final String SIDEBAR_LOCATION1 = "location 1";
    private static final String SIDEBAR_LOCATION2 = "location 2";
    private static final List<SidebarLocation> EXPECTED_SIDEBAR_LOCATIONS = Arrays.asList(
        new SidebarLocation(1, SIDEBAR_LOCATION1),
        new SidebarLocation(2, SIDEBAR_LOCATION2)
    );
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminSidebarLocationService adminService;

    @Test
    void shouldRetrieveAllSidebarLocations() throws Exception {
        when(adminService.getAllSidebarLocations()).thenReturn(EXPECTED_SIDEBAR_LOCATIONS);
        final String sidebarLocationJson  = OBJECT_MAPPER.writeValueAsString(EXPECTED_SIDEBAR_LOCATIONS);

        mockMvc.perform(get(PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(sidebarLocationJson));
    }
}
