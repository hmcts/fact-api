package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtGeneralInfoService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.util.MvcSecurityUtil;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;

@WebMvcTest(AdminCourtGeneralInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourtGeneralInfoControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String CHILD_PATH = "/generalInfo";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String TEST_USER = "mosh@cat.com";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String JSON_NOT_FOUND_TEST_SLUG = String.format(MESSAGE, NOT_FOUND + TEST_SLUG);
    private static final CourtGeneralInfo COURT_GENERAL_INFO = new CourtGeneralInfo(
        "Test court name",
        true,
        true,
        true,
        "English info",
        "Welsh info",
        "English alert",
        "Welsh alert",
        "intro paragraph",
        "intro paragraph cy",
        false,
        false
    );

    private static String courtGeneralInfoJson;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private AdminCourtGeneralInfoService adminService;

    @MockitoBean
    private AdminCourtLockService adminCourtLockService;

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        courtGeneralInfoJson = new ObjectMapper().writeValueAsString(COURT_GENERAL_INFO);
    }

    @Test
    void retrieveGeneralInfoShouldReturnExpectedValue() throws Exception {
        when(adminService.getCourtGeneralInfoBySlug(TEST_SLUG)).thenReturn(COURT_GENERAL_INFO);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + CHILD_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(courtGeneralInfoJson));
    }

    @Test
    void retrieveGeneralInfoShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtGeneralInfoBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + CHILD_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));
    }

    @Test
    void updateGeneralInfoShouldReturnExpectedValue() throws Exception {
        when(adminService.updateCourtGeneralInfo(TEST_SLUG, COURT_GENERAL_INFO)).thenReturn(COURT_GENERAL_INFO);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
                            .with(csrf())
                            .content(courtGeneralInfoJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(courtGeneralInfoJson));
        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void updateGeneralInfoShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.updateCourtGeneralInfo(TEST_SLUG, COURT_GENERAL_INFO)).thenThrow(new NotFoundException(
            TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
                            .with(csrf())
                            .content(courtGeneralInfoJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }
}
