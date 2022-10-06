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
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtGeneralInfoService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCourtGeneralInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtGeneralInfoControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String CHILD_PATH = "/generalInfo";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "message";
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

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtGeneralInfoService adminService;

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
            .andExpect(content().json("{\"" + MESSAGE + "\":\"" + NOT_FOUND + TEST_SLUG + "\"}"));
    }

    @Test
    void updateGeneralInfoShouldReturnExpectedValue() throws Exception {
        when(adminService.updateCourtGeneralInfo(TEST_SLUG, COURT_GENERAL_INFO)).thenReturn(COURT_GENERAL_INFO);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
                            .content(courtGeneralInfoJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(courtGeneralInfoJson));
    }

    @Test
    void updateGeneralInfoShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.updateCourtGeneralInfo(TEST_SLUG, COURT_GENERAL_INFO)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
                            .content(courtGeneralInfoJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"" + MESSAGE + "\":\"" + NOT_FOUND + TEST_SLUG + "\"}"));
    }
}
