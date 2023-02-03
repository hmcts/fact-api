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
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLocalAuthoritiesService;
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

@WebMvcTest(AdminCourtLocalAuthoritiesController.class)
@AutoConfigureMockMvc(addFilters = false)

class AdminCourtLocalAuthoritiesControllerTest {

    private static final String BASE_PATH = "/admin/courts/";
    private static final String CHILD_PATH = "/localAuthorities";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String TEST_USER = "mosh@cat.com";
    private static final String TEST_AREA_OF_LAW = "AreaOfLaw";
    private static final String SLASH = "/";
    private static final String TEST_LOCAL_AUTHORITIES_PATH = "local-authorities.json";
    private static final String TEST_UNKNOWN_COURT_TYPE_MESSAGE = "Court local authority not found";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String JSON_NOT_FOUND_TEST_SLUG = String.format(MESSAGE, NOT_FOUND + TEST_SLUG);
    private static final String JSON_NOT_FOUND_TEST_AREA_OF_LAW = String.format(MESSAGE, NOT_FOUND + TEST_AREA_OF_LAW);
    private static final String JSON_TEST_UNKNOWN_COURT_TYPE_MESSAGE = String.format(MESSAGE, TEST_UNKNOWN_COURT_TYPE_MESSAGE);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtLocalAuthoritiesService adminService;

    @MockBean
    private AdminCourtLockService adminCourtLockService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

    @Test
    void shouldReturnCourtLocalAuthorities() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_LOCAL_AUTHORITIES_PATH);
        final List<LocalAuthority> localAuthorities = asList(OBJECT_MAPPER.readValue(expectedJson, LocalAuthority[].class));

        when(adminService.getCourtLocalAuthoritiesBySlugAndAreaOfLaw(TEST_SLUG, TEST_AREA_OF_LAW)).thenReturn(localAuthorities);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void retrieveCourtLocalAuthoritiesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtLocalAuthoritiesBySlugAndAreaOfLaw(TEST_SLUG, TEST_AREA_OF_LAW)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));
    }

    @Test
    void retrieveCourtLocalAuthoritiesShouldReturnNotFoundForUnknownAreaOfLaw() throws Exception {
        when(adminService.getCourtLocalAuthoritiesBySlugAndAreaOfLaw(TEST_SLUG, TEST_AREA_OF_LAW)).thenThrow(new NotFoundException(TEST_AREA_OF_LAW));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_AREA_OF_LAW));
    }

    @Test
    void updateCourtLocalAuthoritiesBySlugShouldReturnUpdatedCourtLocalAuthorities() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_LOCAL_AUTHORITIES_PATH);
        final List<LocalAuthority> localAuthorities = asList(OBJECT_MAPPER.readValue(expectedJson, LocalAuthority[].class));

        when(adminService.updateCourtLocalAuthority(TEST_SLUG,TEST_AREA_OF_LAW, localAuthorities)).thenReturn(localAuthorities);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH)
                            .with(csrf())
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void updateCourtLocalAuthoritiesBySlugShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_LOCAL_AUTHORITIES_PATH);
        final List<LocalAuthority> localAuthorities = asList(OBJECT_MAPPER.readValue(expectedJson, LocalAuthority[].class));

        when(adminService.updateCourtLocalAuthority(TEST_SLUG,TEST_AREA_OF_LAW, localAuthorities)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH)
                            .with(csrf())
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void updateCourtLocalAuthoritiesBySlugShouldReturnNotFoundForUnknownAreaOfLaw() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_LOCAL_AUTHORITIES_PATH);
        final List<LocalAuthority> localAuthorities = asList(OBJECT_MAPPER.readValue(expectedJson, LocalAuthority[].class));

        when(adminService.updateCourtLocalAuthority(TEST_SLUG,TEST_AREA_OF_LAW,localAuthorities)).thenThrow(new NotFoundException(TEST_AREA_OF_LAW));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH)
                            .with(csrf())
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_AREA_OF_LAW));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void updateCourtLocalAuthoritiesShouldReturnBadRequestForUnknownCourtSlug() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_LOCAL_AUTHORITIES_PATH);
        final List<LocalAuthority> localAuthorities = asList(OBJECT_MAPPER.readValue(expectedJson, LocalAuthority[].class));

        when(adminService.updateCourtLocalAuthority(TEST_SLUG,TEST_AREA_OF_LAW,localAuthorities)).thenThrow(new IllegalArgumentException(TEST_UNKNOWN_COURT_TYPE_MESSAGE));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH)
                            .with(csrf())
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(JSON_TEST_UNKNOWN_COURT_TYPE_MESSAGE));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }
}
