package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.model.admin.County;
import uk.gov.hmcts.dts.fact.services.admin.AdminCountyService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCountyController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCountyControllerTest {
    private static final String PATH = "/admin/counties";
    private static final List<County> EXPECTED_COUNTIES = Arrays.asList(
        new County(1, "West Midlands", "England"),
        new County(2, "Aberdeenshire", "Scotland"),
        new County(3,"Cardiff","Wales")
    );
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockitoBean
    private AdminCountyService adminService;

    @Test
    void shouldRetrieveAllCounties() throws Exception {
        when(adminService.getAllCounties()).thenReturn(EXPECTED_COUNTIES);
        final String countiesJson  = OBJECT_MAPPER.writeValueAsString(EXPECTED_COUNTIES);

        mockMvc.perform(get(PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(countiesJson));

    }
}
