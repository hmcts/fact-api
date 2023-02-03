package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.SpoeAreaOfLaw;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtSpoeAreasOfLawService;
import uk.gov.hmcts.dts.fact.util.MvcSecurityUtil;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.util.TestHelper.getResourceAsJson;

@WebMvcTest(AdminCourtSpoeAreasOfLawController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourtSpoeAreasOfLawControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String CHILD_PATH = "/SpoeAreasOfLaw";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String TEST_USER = "mosh@cat.com";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String JSON_NOT_FOUND_TEST_SLUG = String.format(MESSAGE, NOT_FOUND + TEST_SLUG);
    private static final String TEST_COURT_AREAS_OF_LAW_PATH = "court-spoe-areas-of-law.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtSpoeAreasOfLawService adminService;

    @MockBean
    private AdminCourtLockService adminCourtLockService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

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
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));
    }

    @Test
    void updateCourtSpoeAreasOfLawShouldReturnUpdatedCourtSpoeAreasOfLaw() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<SpoeAreaOfLaw> spoeAreasOfLaw = asList(OBJECT_MAPPER.readValue(expectedJson, SpoeAreaOfLaw[].class));

        when(adminService.updateSpoeAreasOfLawForCourt(TEST_SLUG, spoeAreasOfLaw)).thenReturn(spoeAreasOfLaw);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
                            .with(csrf())
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void updateCourtSpoeAreasOfLawShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        final String jsonBody = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<SpoeAreaOfLaw> spoeAreasOfLaw = asList(OBJECT_MAPPER.readValue(jsonBody, SpoeAreaOfLaw[].class));
        when(adminService.updateSpoeAreasOfLawForCourt(TEST_SLUG, spoeAreasOfLaw))
            .thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
                            .with(csrf())
                            .content(jsonBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }
}
