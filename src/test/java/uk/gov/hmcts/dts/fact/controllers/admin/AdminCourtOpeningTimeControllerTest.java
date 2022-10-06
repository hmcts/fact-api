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
import uk.gov.hmcts.dts.fact.model.admin.OpeningTime;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtOpeningTimeService;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.util.TestHelper.getResourceAsJson;

@WebMvcTest(AdminCourtOpeningTimeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtOpeningTimeControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String OPENING_TIMES_PATH = "/" + "openingTimes";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "message";
    private static final String TEST_OPENING_TIMES_FILE = "opening-times.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtOpeningTimeService adminService;

    @Test
    void retrieveOpeningTimesShouldReturnCourtOpeningTimes() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_OPENING_TIMES_FILE);
        final List<OpeningTime> openingTimes = asList(OBJECT_MAPPER.readValue(expectedJson, OpeningTime[].class));

        when(adminService.getCourtOpeningTimesBySlug(TEST_SLUG)).thenReturn(openingTimes);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + OPENING_TIMES_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void retrieveOpeningTimeShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtOpeningTimesBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + OPENING_TIMES_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"" + MESSAGE + "\":\"" + NOT_FOUND + TEST_SLUG + "\"}"));
    }

    @Test
    void updateOpeningTimesShouldReturnUpdatedCourtOpeningTimes() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_OPENING_TIMES_FILE);
        final List<OpeningTime> openingTimes = asList(OBJECT_MAPPER.readValue(expectedJson, OpeningTime[].class));

        when(adminService.updateCourtOpeningTimes(TEST_SLUG, openingTimes)).thenReturn(openingTimes);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + OPENING_TIMES_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void updateOpeningTimesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        final String json = getResourceAsJson(TEST_OPENING_TIMES_FILE);
        final List<OpeningTime> openingTimes = asList(OBJECT_MAPPER.readValue(json, OpeningTime[].class));

        when(adminService.updateCourtOpeningTimes(TEST_SLUG, openingTimes)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + OPENING_TIMES_PATH)
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"" + MESSAGE + "\":\"" + NOT_FOUND + TEST_SLUG + "\"}"));
    }


}
