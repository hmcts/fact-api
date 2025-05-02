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
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtEmailService;
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

@WebMvcTest(AdminCourtEmailController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourtEmailsControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String ALL_EMAILS_PATH = "/emails";
    private static final String ALL_EMAIL_TYPES_PATH = "emailTypes";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String JSON_NOT_FOUND_TEST_SLUG = String.format(MESSAGE, NOT_FOUND + TEST_SLUG);
    private static final String TEST_EMAILS_FILE = "emails.json";
    private static final String TEST_EMAIL_TYPES_FILE = "email-types.json";
    private static final String TEST_USER = "mosh@cat.com";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockitoBean
    private AdminCourtEmailService adminService;

    @MockitoBean
    private AdminCourtLockService adminCourtLockService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

    @Test
    void retrieveEmailsShouldReturnEmails() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_EMAILS_FILE);
        final List<Email> openingTimes = asList(OBJECT_MAPPER.readValue(expectedJson, Email[].class));

        when(adminService.getCourtEmailsBySlug(TEST_SLUG)).thenReturn(openingTimes);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + ALL_EMAILS_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void retrieveEmailsShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtEmailsBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + ALL_EMAILS_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));
    }

    @Test
    void updateEmailsShouldReturnUpdatedEmails() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_EMAILS_FILE);
        final List<Email> emails = asList(OBJECT_MAPPER.readValue(expectedJson, Email[].class));

        when(adminService.updateEmailListForCourt(TEST_SLUG, emails)).thenReturn(emails);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + ALL_EMAILS_PATH)
                            .with(csrf())
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void updateEmailsShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        final String json = getResourceAsJson(TEST_EMAILS_FILE);
        final List<Email> emails = asList(OBJECT_MAPPER.readValue(json, Email[].class));

        when(adminService.updateEmailListForCourt(TEST_SLUG, emails)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + ALL_EMAILS_PATH)
                            .with(csrf())
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void retrieveEmailTypesShouldReturnAllEmailTypes() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_EMAIL_TYPES_FILE);
        final List<EmailType> emailTypes = asList(OBJECT_MAPPER.readValue(expectedJson, EmailType[].class));

        when(adminService.getAllEmailTypes()).thenReturn(emailTypes);

        mockMvc.perform(get(BASE_PATH + ALL_EMAIL_TYPES_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }
}
