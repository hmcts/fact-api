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
import uk.gov.hmcts.dts.fact.model.admin.AdditionalLink;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAdditionalLinkService;
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

@WebMvcTest(AdminCourtAdditionalLinkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtAdditionalLinkControllerTest {
    private static final String TEST_SLUG = "court-slug";
    private static final String TEST_USER = "mosh@cat.com";
    private static final String BASE_PATH = "/admin/courts/";
    private static final String ADDITIONAL_LINKS_PATH = "/additionalLinks";

    private static final String TEST_URL1 = "www.test1.com";
    private static final String TEST_URL2 = "www.test2.com";
    private static final String TEST_URL3 = "www.test3.com";

    private static final String TEST_DESCRIPTION1 = "description 1";
    private static final String TEST_DESCRIPTION2 = "description 2";
    private static final String TEST_DESCRIPTION3 = "description 3";

    private static final String TEST_DESCRIPTION_CY1 = "description cy 1";
    private static final String TEST_DESCRIPTION_CY2 = "description cy 2";
    private static final String TEST_DESCRIPTION_CY3 = "description cy 3";

    private static final List<AdditionalLink> EXPECTED_ADDITIONAL_LINKS = Arrays.asList(
        new AdditionalLink(TEST_URL1, TEST_DESCRIPTION1, TEST_DESCRIPTION_CY1),
        new AdditionalLink(TEST_URL2, TEST_DESCRIPTION2, TEST_DESCRIPTION_CY2),
        new AdditionalLink(TEST_URL3, TEST_DESCRIPTION3, TEST_DESCRIPTION_CY3)
    );
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static String additionalLinksJson;
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String JSON_NOT_FOUND_TEST_SLUG = String.format(MESSAGE, NOT_FOUND + TEST_SLUG);

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtAdditionalLinkService adminService;

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
        additionalLinksJson = OBJECT_MAPPER.writeValueAsString(EXPECTED_ADDITIONAL_LINKS);
    }

    @Test
    void shouldRetrieveCourtAdditionalLinks() throws Exception {
        when(adminService.getCourtAdditionalLinksBySlug(TEST_SLUG)).thenReturn(EXPECTED_ADDITIONAL_LINKS);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + ADDITIONAL_LINKS_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(additionalLinksJson));
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingAdditionalLinksForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtAdditionalLinksBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + ADDITIONAL_LINKS_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));
    }

    @Test
    void shouldUpdateCourtAdditionalLinks() throws Exception {
        when(adminService.updateCourtAdditionalLinks(TEST_SLUG, EXPECTED_ADDITIONAL_LINKS)).thenReturn(EXPECTED_ADDITIONAL_LINKS);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + ADDITIONAL_LINKS_PATH)
                            .with(csrf())
                            .content(additionalLinksJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(additionalLinksJson));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingAdditionalLinksForUnknownCourtSlug() throws Exception {
        when(adminService.updateCourtAdditionalLinks(TEST_SLUG, EXPECTED_ADDITIONAL_LINKS)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + ADDITIONAL_LINKS_PATH)
                            .with(csrf())
                            .content(additionalLinksJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));

        verify(adminCourtLockService, never()).updateCourtLock(TEST_SLUG, TEST_USER);
    }
}
