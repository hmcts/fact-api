package uk.gov.hmcts.dts.fact.controllers.admin.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminAreasOfLawService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAreasOfLawController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminAreasOfLawControllerTest {
    private static final String BASE_PATH = "/admin/courtAreasOfLaw";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminAreasOfLawService adminAreasOfLawService;

    @Test
    void shouldReturnAllAreasOfLaw() throws Exception {

        final List<AreaOfLaw> mockAllAreasOfLaw = Arrays.asList(
            new AreaOfLaw(new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(100, "Divorce")),
            new AreaOfLaw(new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(200, "Adoption")),
            new AreaOfLaw(new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(300, "children")),
            new AreaOfLaw(new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(400, "Money"))
        );
        when(adminAreasOfLawService.getAllAreasOfLaw()).thenReturn(mockAllAreasOfLaw);

        final String allAreasOfLawJson = new ObjectMapper().writeValueAsString(mockAllAreasOfLaw);

        mockMvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(allAreasOfLawJson));
    }
}
