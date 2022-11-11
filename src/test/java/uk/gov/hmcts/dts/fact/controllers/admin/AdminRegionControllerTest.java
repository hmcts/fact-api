package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.model.admin.Region;
import uk.gov.hmcts.dts.fact.services.admin.AdminRegionService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminRegionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminRegionControllerTest {
    private static final String PATH = "/admin/regions";
    private static final List<Region> EXPECTED_REGIONS = Arrays.asList(
        new Region(1, "North West", "England"),
        new Region(2, "East Midlands", "England"),
        new Region(3,"Mid and West Wales","Wales")
    );
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminRegionService adminService;

    @Test
    void shouldRetrieveAllRegions() throws Exception {
        when(adminService.getAllRegions()).thenReturn(EXPECTED_REGIONS);
        final String regionsJson  = OBJECT_MAPPER.writeValueAsString(EXPECTED_REGIONS);

        mockMvc.perform(get(PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(regionsJson));

    }

}
