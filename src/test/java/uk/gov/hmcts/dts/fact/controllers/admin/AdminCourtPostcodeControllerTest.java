package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtPostcodeService;
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCourtPostcodeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtPostcodeControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String POSTCODE_PATH = "/postcodes";
    private static final String TEST_SLUG = "test-slug";
    private static final String NOT_FOUND_PREFIX = "Not found: ";
    private static final List<String> TEST_POSTCODES = Arrays.asList("M11AA", "M11BB", "M11CC");
    private static final List<String> INVALID_POSTCODE = Collections.singletonList("M11CC");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtPostcodeService adminService;

    @MockBean
    private ValidationService validationService;

    private static String expectedPostcodeJson;
    private static String expectedInvalidPostcodesJson;

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        expectedPostcodeJson = OBJECT_MAPPER.writeValueAsString(TEST_POSTCODES);
        expectedInvalidPostcodesJson = OBJECT_MAPPER.writeValueAsString(INVALID_POSTCODE);
    }

    @Test
    void shouldReturnCourtPostcodes() throws Exception {
        when(adminService.getCourtPostcodesBySlug(TEST_SLUG)).thenReturn(TEST_POSTCODES);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + POSTCODE_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedPostcodeJson));
    }

    @Test
    void retrievePostcodesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtPostcodesBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + POSTCODE_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().string(NOT_FOUND_PREFIX + TEST_SLUG));
    }

    @Test
    void addPostcodesShouldReturnCreatedStatus() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(Collections.emptyList());
        when(adminService.addCourtPostcodes(TEST_SLUG, TEST_POSTCODES)).thenReturn(TEST_POSTCODES);

        mockMvc.perform(post(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().json(expectedPostcodeJson));
    }

    @Test
    void addPostcodesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(Collections.emptyList());
        doThrow(new NotFoundException(TEST_SLUG)).when(adminService).addCourtPostcodes(TEST_SLUG, TEST_POSTCODES);

        mockMvc.perform(post(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string(NOT_FOUND_PREFIX + TEST_SLUG));
    }

    @Test
    void addPostcodesShouldReturnBadRequestForInvalidPostcodes() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(INVALID_POSTCODE);

        mockMvc.perform(post(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                           .content(expectedPostcodeJson)
                           .contentType(MediaType.APPLICATION_JSON)
                           .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(expectedInvalidPostcodesJson));

        verifyNoInteractions(adminService);
    }

    @Test
    void deletePostcodesShouldReturnOkStatus() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(Collections.emptyList());
        doNothing().when(adminService).deleteCourtPostcodes(TEST_SLUG, TEST_POSTCODES);

        mockMvc.perform(delete(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void deletePostcodesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(Collections.emptyList());
        doThrow(new NotFoundException(TEST_SLUG)).when(adminService).deleteCourtPostcodes(TEST_SLUG, TEST_POSTCODES);

        mockMvc.perform(delete(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string(NOT_FOUND_PREFIX + TEST_SLUG));
    }

    @Test
    void deletePostcodesShouldReturnBadRequestForInvalidPostcodes() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(INVALID_POSTCODE);

        mockMvc.perform(delete(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(expectedInvalidPostcodesJson));

        verifyNoInteractions(adminService);
    }
}
