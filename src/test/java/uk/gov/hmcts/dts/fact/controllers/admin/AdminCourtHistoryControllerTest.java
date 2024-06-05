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
public class AdminCourtHistoryControllerTest {

    public static final String PATH = "/admin/courts/history";
    private static final List<CourtHistory> FAKE_COURT_HISTORIES = Arrays.asList(
        new CourtHistory(
                1, 11, "fakeCourt1", LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2007-12-03T10:15:30"), null),
        new CourtHistory(
                2, 11, "fakeCourt1", null, null, null),
        new CourtHistory(
                3, 11, "fakeCourt1", LocalDateTime.parse("2024-02-03T10:15:30"),
                LocalDateTime.parse("2023-12-03T11:15:30"), ""),
        new CourtHistory(
                4, 11, "fakeCourt1", LocalDateTime.parse("2024-02-03T10:15:30"),
                LocalDateTime.parse("2024-03-03T10:15:30"), "Llys y Goron")
    );

    private static final CourtHistory FAKE_COURT_HISTORY = new CourtHistory(
        4, 12, "fakeCourt4", LocalDateTime.parse("2024-02-03T10:15:30"),
        LocalDateTime.parse("2024-03-03T10:15:30"), "Llys y Goron");

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

        final String courtHistoriesJSON = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORIES);
        mockMvc.perform(get(PATH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoriesJSON));
    }

    @Test
    void shouldRetrieveACourtHistoryByID() throws Exception {
        when(adminCourtHistoryService.getCourtHistoryById(4)).thenReturn(FAKE_COURT_HISTORY);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJSON = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORY);
        mockMvc.perform(get(PATH + "/4"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoryJSON));
    }

    @Test
    void shouldNotRetrieveNonExistentCourtHistory() throws Exception {
        when(adminCourtHistoryService.getCourtHistoryById(13)).thenThrow(new NotFoundException("Court History with ID: " + 13));

        mockMvc.perform(get(PATH + "/13"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldRetrieveCourtHistoriesByCourtID() throws Exception {
        when(adminCourtHistoryService.getCourtHistoryByCourtId(12)).thenReturn(FAKE_COURT_HISTORIES);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJSON = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORIES);
        mockMvc.perform(get(PATH + "/id/12"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoryJSON));
    }

    @Test
    void shouldRetrieveCourtHistoriesByName() throws Exception {
        when(adminCourtHistoryService.getCourtHistoryByCourtName("fakeCourt1")).thenReturn(FAKE_COURT_HISTORIES);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJSON = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORIES);
        mockMvc.perform(get(PATH + "/name/fakeCourt1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoryJSON));
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

        final String courtHistoryJSON = OBJECT_MAPPER.writeValueAsString(newFakeCourtHistory);

        mockMvc.perform(post(PATH + "/")
                            .content(courtHistoryJSON)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated())
            .andReturn();
    }

    @Test
    void shouldUpdateCourtHistory() throws Exception {
        CourtHistory updatedCourtHistory = new CourtHistory(1, 21, "fakeCourt2",
                                                            LocalDateTime.parse("2024-02-03T10:15:30"),
                                                            LocalDateTime.parse("2024-02-03T10:15:30"),
                                                            null);
        when(adminCourtHistoryService.updateCourtHistory(updatedCourtHistory)).thenReturn(updatedCourtHistory);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJSON = OBJECT_MAPPER.writeValueAsString(updatedCourtHistory);

        mockMvc.perform(put(PATH + "/")
                            .content(courtHistoryJSON)
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

        final String courtHistoryJSON = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORY);

        mockMvc.perform(delete(PATH + "/" + 4))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(content().string(courtHistoryJSON));

    }

    @Test
    void shouldNotBeAbleToDeleteACourtThatDoesNotExist() throws Exception{
        when(adminCourtHistoryService.deleteCourtHistoryById(13)).thenThrow(new NotFoundException("Court History with ID: " + 13));

        mockMvc.perform(delete(PATH + "/13"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeAbleToDeleteAllCourtHistoryOfACourtById() throws Exception {
        when(adminCourtHistoryService.deleteCourtHistoriesByCourtId(11)).thenReturn(FAKE_COURT_HISTORIES);

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String courtHistoryJSON = OBJECT_MAPPER.writeValueAsString(FAKE_COURT_HISTORIES);

        mockMvc.perform(delete(PATH + "/id/" + 11))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(courtHistoryJSON));
    }
}
