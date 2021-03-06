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
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLocalAuthoritiesService;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.util.TestHelper.getResourceAsJson;

@WebMvcTest(AdminCourtLocalAuthoritiesController.class)
@AutoConfigureMockMvc(addFilters = false)

public class AdminCourtLocalAuthoritiesControllerTest {

    private static final String BASE_PATH = "/admin/courts/";
    private static final String CHILD_PATH = "/localAuthorities";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String TEST_AREA_OF_LAW = "AreaOfLaw";
    private static final String SLASH = "/";
    private static final String TEST_LOCAL_AUTHORITIES_PATH = "local-authorities.json";
    private static final String TEST_UNKNOWN_COURT_TYPE_MESSAGE = "Court local authority not found";
    private static final String NOT_FOUND = "Not found: ";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtLocalAuthoritiesService adminService;

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
            .andExpect(content().string(NOT_FOUND + TEST_SLUG));
    }

    @Test
    void retrieveCourtLocalAuthoritiesShouldReturnNotFoundForUnknownAreaOfLaw() throws Exception {
        when(adminService.getCourtLocalAuthoritiesBySlugAndAreaOfLaw(TEST_SLUG, TEST_AREA_OF_LAW)).thenThrow(new NotFoundException(TEST_AREA_OF_LAW));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().string(NOT_FOUND + TEST_AREA_OF_LAW));
    }

    @Test
    void updateCourtLocalAuthoritiesBySlugShouldReturnUpdatedCourtLocalAuthorities() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_LOCAL_AUTHORITIES_PATH);
        final List<LocalAuthority> localAuthorities = asList(OBJECT_MAPPER.readValue(expectedJson, LocalAuthority[].class));

        when(adminService.updateCourtLocalAuthority(TEST_SLUG,TEST_AREA_OF_LAW, localAuthorities)).thenReturn(localAuthorities);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void updateCourtLocalAuthoritiesBySlugShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_LOCAL_AUTHORITIES_PATH);
        final List<LocalAuthority> localAuthorities = asList(OBJECT_MAPPER.readValue(expectedJson, LocalAuthority[].class));

        when(adminService.updateCourtLocalAuthority(TEST_SLUG,TEST_AREA_OF_LAW, localAuthorities)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string(NOT_FOUND + TEST_SLUG));
    }

    @Test
    void updateCourtLocalAuthoritiesBySlugShouldReturnNotFoundForUnknownAreaOfLaw() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_LOCAL_AUTHORITIES_PATH);
        final List<LocalAuthority> localAuthorities = asList(OBJECT_MAPPER.readValue(expectedJson, LocalAuthority[].class));

        when(adminService.updateCourtLocalAuthority(TEST_SLUG,TEST_AREA_OF_LAW,localAuthorities)).thenThrow(new NotFoundException(TEST_AREA_OF_LAW));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string(NOT_FOUND + TEST_AREA_OF_LAW));
    }

    @Test
    void updateCourtLocalAuthoritiesShouldReturnBadRequestForUnknownCourtSlug() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_LOCAL_AUTHORITIES_PATH);
        final List<LocalAuthority> localAuthorities = asList(OBJECT_MAPPER.readValue(expectedJson, LocalAuthority[].class));

        when(adminService.updateCourtLocalAuthority(TEST_SLUG,TEST_AREA_OF_LAW,localAuthorities)).thenThrow(new IllegalArgumentException(TEST_UNKNOWN_COURT_TYPE_MESSAGE));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + SLASH + TEST_AREA_OF_LAW + CHILD_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(TEST_UNKNOWN_COURT_TYPE_MESSAGE));
    }
}
