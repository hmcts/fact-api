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
import uk.gov.hmcts.dts.fact.model.admin.SpoeAreaOfLaw;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtSpoeAreasOfLawService;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.util.TestHelper.getResourceAsJson;

@WebMvcTest(AdminCourtSpoeAreasOfLawController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtSpoeAreasOfLawControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String CHILD_PATH = "/SpoeAreasOfLaw";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "message";
    private static final String TEST_COURT_AREAS_OF_LAW_PATH = "court-spoe-areas-of-law.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtSpoeAreasOfLawService adminService;


    @Test
    void shouldReturnAllSpoeAreasOfLaw() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<SpoeAreaOfLaw> spoeAreasOfLaw = asList(OBJECT_MAPPER.readValue(expectedJson, SpoeAreaOfLaw[].class));

        when(adminService.getAllSpoeAreasOfLaw()).thenReturn(spoeAreasOfLaw);

        mockMvc.perform(get(BASE_PATH + CHILD_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }



    @Test
    void shouldReturnCourtSpoeAreasOfLaw() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<SpoeAreaOfLaw> spoeAreasOfLaw = asList(OBJECT_MAPPER.readValue(expectedJson, SpoeAreaOfLaw[].class));

        when(adminService.getCourtSpoeAreasOfLawBySlug(TEST_SLUG)).thenReturn(spoeAreasOfLaw);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + CHILD_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void retrieveCourtSpoeAreasOfLawAndShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtSpoeAreasOfLawBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + CHILD_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"" + MESSAGE + "\":\"" + NOT_FOUND + TEST_SLUG + "\"}"));
    }

    @Test
    void updateCourtSpoeAreasOfLawShouldReturnUpdatedCourtSpoeAreasOfLaw() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<SpoeAreaOfLaw> spoeAreasOfLaw = asList(OBJECT_MAPPER.readValue(expectedJson, SpoeAreaOfLaw[].class));

        when(adminService.updateSpoeAreasOfLawForCourt(TEST_SLUG, spoeAreasOfLaw)).thenReturn(spoeAreasOfLaw);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
            .content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void updateCourtSpoeAreasOfLawShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        final String jsonBody = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<SpoeAreaOfLaw> spoeAreasOfLaw = asList(OBJECT_MAPPER.readValue(jsonBody, SpoeAreaOfLaw[].class));
        when(adminService.updateSpoeAreasOfLawForCourt(TEST_SLUG, spoeAreasOfLaw))
            .thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"" + MESSAGE + "\":\"" + NOT_FOUND + TEST_SLUG + "\"}"));
    }
}
