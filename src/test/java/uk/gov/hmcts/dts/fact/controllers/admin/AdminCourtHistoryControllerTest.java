package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtHistory;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtHistoryService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCourtHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourtHistoryControllerTest {

    public static final String PATH = "/admin/courts";
    private static final String PATH_SUFFIX = "/history";
    private static final String DATE = "2024-02-03T10:15:30";
    private static final String FAKE_COURT_NAME1 = "fakeCourt1";
    private static final String FAKE_COURT_SLUG = "fake-court-slug";
    private static final List<CourtHistory> FAKE_COURT_HISTORIES = Arrays.asList(
        new CourtHistory(
                1, 11, FAKE_COURT_NAME1, LocalDateTime.parse(DATE),
            LocalDateTime.parse(DATE), null),
        new CourtHistory(
                2, 11, FAKE_COURT_NAME1, null, null, null),
        new CourtHistory(
                3, 11, FAKE_COURT_NAME1, LocalDateTime.parse(DATE),
                LocalDateTime.parse(DATE), ""),
        new CourtHistory(
                4, 11, FAKE_COURT_NAME1, LocalDateTime.parse(DATE),
                LocalDateTime.parse(DATE), "Llys y Goron")
    );

    private static final CourtHistory FAKE_COURT_HISTORY = new CourtHistory(
        4, 12, "fakeCourt4", LocalDateTime.parse(DATE),
        LocalDateTime.parse(DATE), "Llys y Goron");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    AdminCourtHistoryService adminCourtHistoryService;


    @Test
    void shouldRetrieveAllCourtHistories() throws Exception {
        when(adminCourtHistoryService.getAllCourtHistory()).thenReturn(FAKE_COURT_HISTORIES);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoriesJson = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORIES);
        mockMvc.perform(get(PATH + PATH_SUFFIX))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoriesJson));
    }

    @Test
    void shouldRetrieveACourtHistoryBySlug() throws Exception {
        when(adminCourtHistoryService.getCourtHistoryByCourtSlug("i-am-a-slug")).thenReturn(FAKE_COURT_HISTORIES);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJson = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORIES);
        mockMvc.perform(get(PATH + "/i-am-a-slug" + PATH_SUFFIX))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoryJson));
    }

    @Test
    void shouldRetrieveACourtHistoryById() throws Exception {
        when(adminCourtHistoryService.getCourtHistoryById(4)).thenReturn(FAKE_COURT_HISTORY);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJson = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORY);
        mockMvc.perform(get(PATH + "/id/4" + PATH_SUFFIX))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoryJson));
    }

    @Test
    void shouldNotRetrieveNonExistentCourtHistory() throws Exception {
        when(adminCourtHistoryService.getCourtHistoryById(13)).thenThrow(new NotFoundException("Court History with ID: " + 13));

        mockMvc.perform(get(PATH + "id/13" + PATH_SUFFIX))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldRetrieveCourtHistoriesByCourtId() throws Exception {
        when(adminCourtHistoryService.getCourtHistoryByCourtId(12)).thenReturn(FAKE_COURT_HISTORIES);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJson = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORIES);
        mockMvc.perform(get(PATH + "/court-id/12" + PATH_SUFFIX))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoryJson));
    }

    @Test
    void shouldRetrieveCourtHistoriesByName() throws Exception {
        when(adminCourtHistoryService.getCourtHistoryByCourtName("fakeCourt1")).thenReturn(FAKE_COURT_HISTORIES);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJson = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORIES);
        mockMvc.perform(get(PATH + "/name/fakeCourt1" + PATH_SUFFIX))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoryJson));
    }

    @Test
    void shouldAddCourtHistory() throws Exception {
        CourtHistory newFakeCourtHistory = new CourtHistory(null, 21, "fakeCourt2",
                                                            null,
                                                            null,
                                                            null);
        when(adminCourtHistoryService.addCourtHistory(newFakeCourtHistory)).thenReturn(newFakeCourtHistory);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJson = OBJECT_MAPPER.writeValueAsString(newFakeCourtHistory);

        mockMvc.perform(post(PATH + "/" + PATH_SUFFIX)
                            .content(courtHistoryJson)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated())
            .andReturn();
    }

    @Test
    void shouldUpdateOneCourtHistory() throws Exception {
        CourtHistory updatedCourtHistory = new CourtHistory(1, 21, "fakeCourt2",
                                                            LocalDateTime.parse(DATE),
                                                            LocalDateTime.parse(DATE),
                                                            null);
        when(adminCourtHistoryService.updateCourtHistory(updatedCourtHistory)).thenReturn(updatedCourtHistory);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJson = OBJECT_MAPPER.writeValueAsString(updatedCourtHistory);

        mockMvc.perform(put(PATH + PATH_SUFFIX)
                            .content(courtHistoryJson)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    void shouldBeAbleToDeleteACourtHistoryById() throws Exception {
        when(adminCourtHistoryService.deleteCourtHistoryById(4)).thenReturn(FAKE_COURT_HISTORY);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJson = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORY);

        mockMvc.perform(delete(PATH + "/id/" + 4 + PATH_SUFFIX))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(content().string(courtHistoryJson));

    }

    @Test
    void shouldNotBeAbleToDeleteACourtThatDoesNotExist() throws Exception {
        when(adminCourtHistoryService.deleteCourtHistoryById(13)).thenThrow(new NotFoundException("Court History with ID: " + 13));

        mockMvc.perform(delete(PATH + "/13"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeAbleToDeleteAllCourtHistoryOfACourtById() throws Exception {
        when(adminCourtHistoryService.deleteCourtHistoriesByCourtId(11)).thenReturn(FAKE_COURT_HISTORIES);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJson = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORIES);

        mockMvc.perform(delete(PATH + "/court-id/" + 11 + PATH_SUFFIX))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoryJson));
    }

    @Test
    void shouldBeAbleToUpdateCourtHistoriesOfaGivenCourt() throws Exception {
        when(adminCourtHistoryService.updateCourtHistoriesBySlug(FAKE_COURT_SLUG, FAKE_COURT_HISTORIES)).thenReturn(FAKE_COURT_HISTORIES);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJson = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORIES);

        mockMvc.perform(put(PATH + "/" + FAKE_COURT_SLUG + PATH_SUFFIX)
                            .content(courtHistoryJson)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();
    }
}
