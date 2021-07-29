package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAreasOfLawService;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.util.TestHelper.getResourceAsJson;

@WebMvcTest(AdminCourtAreasOfLawController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtAreasOfLawControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String CHILD_PATH = "/courtAreasOfLaw";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String TEST_COURT_AREAS_OF_LAW_PATH = "court-areas-of-law.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtAreasOfLawService adminService;


    @Test
    void shouldReturnCourAreasOfLaw() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<AreaOfLaw> areasOfLaw = asList(OBJECT_MAPPER.readValue(expectedJson, AreaOfLaw[].class));

        when(adminService.getCourtAreasOfLawBySlug(TEST_SLUG)).thenReturn(areasOfLaw);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + CHILD_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void retrieveCourtLocalAuthoritiesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtAreasOfLawBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + CHILD_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Not found: " + TEST_SLUG));
    }

    @Test
    void updateCourtAreasOfLawShouldReturnUpdatedCourtAreasOfLaw() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<AreaOfLaw> areasOfLaw = asList(OBJECT_MAPPER.readValue(expectedJson, AreaOfLaw[].class));

        when(adminService.updateAreasOfLawForCourt(TEST_SLUG, areasOfLaw)).thenReturn(areasOfLaw);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
                    .content(expectedJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

}
