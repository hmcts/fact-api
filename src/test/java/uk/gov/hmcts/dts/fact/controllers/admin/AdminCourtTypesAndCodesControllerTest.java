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
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.model.admin.CourtTypesAndCodes;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtTypesAndCodesService;
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

@WebMvcTest(AdminCourtTypesAndCodesController.class)
@AutoConfigureMockMvc(addFilters = false)

class AdminCourtTypesAndCodesControllerTest {


    private static final String BASE_PATH = "/admin/courts/";
    private static final String COURT_TYPES_AND_CODES_PATH = "/courtTypesAndCodes";
    private static final String COURT_TYPES_PATH = "courtTypes";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String TEST_USER = "mosh@cat.com";
    private static final String NOT_FOUND = "Not found: ";
    private static final String TEST_COURT_TYPES_PATH = "court-types.json";
    private static final String TEST_COURT_TYPES_AND_CODES_PATH = "court-types-and-codes.json";
    private static final String TEST_UNKNOWN_COURT_TYPE_MESSAGE = "Court type not found";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String JSON_NOT_FOUND_TEST_SLUG = String.format(MESSAGE, NOT_FOUND + TEST_SLUG);
    private static final String JSON_NOT_FOUND_TEST_UNKNOWN_COURT_TYPE_MESSAGE = String.format(MESSAGE, TEST_UNKNOWN_COURT_TYPE_MESSAGE);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockitoBean
    private AdminCourtLockService adminCourtLockService;

    @MockitoBean
    private AdminCourtTypesAndCodesService adminService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

    @Test
    void shouldReturnAllCourtTypes() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_TYPES_PATH);
        final List<CourtType> courtTypes = asList(OBJECT_MAPPER.readValue(expectedJson, CourtType[].class));

        when(adminService.getAllCourtTypes()).thenReturn(courtTypes);

        mockMvc.perform(get(BASE_PATH + COURT_TYPES_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldReturnCourtTypesAndCodes() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_TYPES_AND_CODES_PATH);
        final CourtTypesAndCodes courtTypesAndCodes = OBJECT_MAPPER.readValue(expectedJson, CourtTypesAndCodes.class);

        when(adminService.getCourtTypesAndCodes(TEST_SLUG)).thenReturn(courtTypesAndCodes);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + COURT_TYPES_AND_CODES_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void retrieveCourtTypesAndCodesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtTypesAndCodes(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + COURT_TYPES_AND_CODES_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));
    }


    @Test
    void updateCourtTypesAndCodesShouldReturnUpdatedCourtTypesAndCodes() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_TYPES_AND_CODES_PATH);
        final CourtTypesAndCodes courtTypesAndCodes = OBJECT_MAPPER.readValue(expectedJson, CourtTypesAndCodes.class);

        when(adminService.updateCourtTypesAndCodes(TEST_SLUG, courtTypesAndCodes)).thenReturn(courtTypesAndCodes);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + COURT_TYPES_AND_CODES_PATH)
                            .with(csrf())
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void updateCourtTypesAndCodesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_TYPES_AND_CODES_PATH);
        final CourtTypesAndCodes courtTypesAndCodes = OBJECT_MAPPER.readValue(expectedJson, CourtTypesAndCodes.class);

        when(adminService.updateCourtTypesAndCodes(TEST_SLUG, courtTypesAndCodes)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + COURT_TYPES_AND_CODES_PATH)
                            .with(csrf())
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void updateCourtTypesAndCodesShouldReturnBadRequestForUnknownCourtType() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_COURT_TYPES_AND_CODES_PATH);
        final CourtTypesAndCodes courtTypesAndCodes = OBJECT_MAPPER.readValue(expectedJson, CourtTypesAndCodes.class);

        when(adminService.updateCourtTypesAndCodes(TEST_SLUG, courtTypesAndCodes)).thenThrow(new IllegalArgumentException(TEST_UNKNOWN_COURT_TYPE_MESSAGE));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + COURT_TYPES_AND_CODES_PATH)
                            .with(csrf())
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_UNKNOWN_COURT_TYPE_MESSAGE));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }
}
