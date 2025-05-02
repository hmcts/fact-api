package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAreasOfLawService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
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

@WebMvcTest(AdminCourtAreasOfLawController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourtAreasOfLawControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String CHILD_PATH = "/courtAreasOfLaw";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String TEST_USER = "mosh@cat.com";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String JSON_NOT_FOUND_TEST_SLUG = String.format(MESSAGE, NOT_FOUND + TEST_SLUG);
    private static final String TEST_COURT_AREAS_OF_LAW_PATH = "court-areas-of-law.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockitoBean
    private AdminCourtAreasOfLawService adminService;

    @MockitoBean
    private AdminCourtLockService adminCourtLockService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

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
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));
    }

    @Test
    void updateCourtAreasOfLawShouldReturnUpdatedCourtAreasOfLaw() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<AreaOfLaw> areasOfLaw = asList(OBJECT_MAPPER.readValue(expectedJson, AreaOfLaw[].class));

        when(adminService.updateAreasOfLawForCourt(TEST_SLUG, areasOfLaw)).thenReturn(areasOfLaw);

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
    void updateCourtLocalAuthoritiesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        final String jsonBody = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<AreaOfLaw> areasOfLaw = asList(OBJECT_MAPPER.readValue(jsonBody, AreaOfLaw[].class));
        when(adminService.updateAreasOfLawForCourt(TEST_SLUG, areasOfLaw))
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
