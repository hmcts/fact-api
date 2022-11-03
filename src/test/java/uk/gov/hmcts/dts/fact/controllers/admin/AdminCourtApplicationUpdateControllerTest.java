package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
import uk.gov.hmcts.dts.fact.model.admin.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtApplicationUpdateService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.util.MvcSecurityUtil;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;

@WebMvcTest(AdminCourtApplicationUpdateController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtApplicationUpdateControllerTest {
    private static final String TEST_SLUG = "court-slug";
    private static final String TEST_USER = "mosh@cat.com";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String JSON_NOT_FOUND_TEST_SLUG = String.format(MESSAGE, NOT_FOUND + TEST_SLUG);
    private static final String BASE_PATH = "/admin/courts/";
    private static final String ADDRESSES_PATH = "/" + "application-progression";

    private static final List<ApplicationUpdate> COURT_APPLICATION_UPDATES = Arrays.asList(
        new ApplicationUpdate("English Type", "Welsh Type", "Email", "External Link",
                              "Link Description English", "Link Description Welsh"),
        new ApplicationUpdate("English Type 2", "Welsh Type 2", "Email 2",
                              "External Link 2", "External Link Description English 2",
                              "External Link Description Welsh 2")
    );

    private static String courtApplicationUpdateJson;

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtApplicationUpdateService adminService;

    @MockBean
    private AdminCourtLockService adminCourtLockService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        courtApplicationUpdateJson = new ObjectMapper().writeValueAsString(COURT_APPLICATION_UPDATES);
    }

    @Test
    void shouldReturnAllApplicationUpdates() throws Exception {
        when(adminService.getApplicationUpdatesBySlug(TEST_SLUG)).thenReturn(COURT_APPLICATION_UPDATES);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + ADDRESSES_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(courtApplicationUpdateJson));
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingApplicationUpdatesForUnknownCourtSlug() throws Exception {
        when(adminService.getApplicationUpdatesBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + ADDRESSES_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));
    }

    @Test
    void shouldUpdateApplicationUpdates() throws Exception {
        when(adminService.updateApplicationUpdates(TEST_SLUG, COURT_APPLICATION_UPDATES)).thenReturn(COURT_APPLICATION_UPDATES);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + ADDRESSES_PATH)
                            .with(csrf())
                            .content(courtApplicationUpdateJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(courtApplicationUpdateJson));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingApplicationUpdatesForUnknownCourtSlug() throws Exception {
        when(adminService.updateApplicationUpdates(TEST_SLUG, COURT_APPLICATION_UPDATES)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + ADDRESSES_PATH)
                            .with(csrf())
                            .content(courtApplicationUpdateJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));

        verify(adminCourtLockService, never()).updateCourtLock(TEST_SLUG, TEST_USER);
    }
}
