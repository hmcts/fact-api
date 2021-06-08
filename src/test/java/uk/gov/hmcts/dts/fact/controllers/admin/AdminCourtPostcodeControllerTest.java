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

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCourtPostcodeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtPostcodeControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String POSTCODE_PATH = "/postcodes";
    private static final String TEST_SLUG = "test-slug";
    private static final List<String> TEST_POSTCODES = Arrays.asList("M11AA", "M11BB", "M11CC");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtPostcodeService adminService;

    private static String expectedJson;

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        expectedJson = OBJECT_MAPPER.writeValueAsString(TEST_POSTCODES);
    }

    @Test
    void shouldReturnCourtPostcodes() throws Exception {
        when(adminService.getCourtPostcodesBySlug(TEST_SLUG)).thenReturn(TEST_POSTCODES);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + POSTCODE_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void retrievePostcodesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtPostcodesBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + POSTCODE_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Not found: " + TEST_SLUG));
    }

    @Test
    void updatePostcodesShouldReturnUpdatedCourtPostcodes() throws Exception {
        when(adminService.updateCourtPostcodes(TEST_SLUG, TEST_POSTCODES)).thenReturn(TEST_POSTCODES);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void updatePostcodesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.updateCourtPostcodes(TEST_SLUG, TEST_POSTCODES)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Not found: " + TEST_SLUG));
    }
}
