package uk.gov.hmcts.dts.fact.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.entity.CourtHistory;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithHistoricalName;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.services.CourtService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.nio.file.Files.readAllBytes;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourtsController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourtsControllerTest {

    private static final String URL = "/courts";
    private static final String SEARCH_BY_PREFIX_AND_ACTIVE_URL = "/courts/search?prefix=a&active=true";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String SEARCH_BY_COURT_TYPES = "/court-types/Family Court,Tribunal";

    private static final String SEARCH_BY_COURT_HISTORY_NAME = "/court-history/search";

    @Autowired
    private transient MockMvc mockMvc;

    @MockitoBean
    private CourtService courtService;

    @Test
    void shouldFindCourtBySlugDeprecated() throws Exception {

        final Path path = Paths.get(
            "src/integrationTest/resources/deprecated/aylesbury-magistrates-court-and-family-court.json");
        final String expectJson = new String(readAllBytes(path));

        final OldCourt court = OBJECT_MAPPER.readValue(path.toFile(), OldCourt.class);

        final String searchSlug = "some-slug";

        when(courtService.getCourtBySlugDeprecated(searchSlug)).thenReturn(court);
        mockMvc.perform(get(String.format(URL + "/%s.json", searchSlug)))
            .andExpect(status().isOk())
            .andExpect(content().json(expectJson))
            .andReturn();
    }

    @Test
    void shouldRespondWithNotFoundForFindCourtByNonExistentSlug() throws Exception {

        final String searchSlug = "some-slug";
        when(courtService.getCourtBySlugDeprecated(searchSlug)).thenThrow(new NotFoundException(searchSlug));

        mockMvc.perform(get(String.format(URL + "/%s.json", searchSlug)))
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    void shouldFindCourtByQuery() throws Exception {

        final Path path = Paths.get("src/test/resources/courts.json");
        final String expectedJson = new String(readAllBytes(path));

        final List<CourtReference> courts = Arrays.asList(OBJECT_MAPPER.readValue(path.toFile(), CourtReference[].class));
        final String query = "london";

        when(courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch(query)).thenReturn(courts);
        mockMvc.perform(get(URL + "?q=" + query))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson))
            .andReturn();
    }

    @Test
    @SuppressWarnings("PMD.AddEmptyString")
    void shouldRespondWithBadRequestForFindCourtByEmptyQuery() throws Exception {

        final List<CourtReference> courts = new ArrayList<>();
        final String query = "";

        when(courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch(query)).thenReturn(courts);

        mockMvc.perform(get(URL + "?q=" + query))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindCourtBySlug() throws Exception {

        final Path path = Paths.get("src/integrationTest/resources/birmingham-civil-and-family-justice-centre.json");
        final String expectedJson = new String(readAllBytes(path));
        final Court court = OBJECT_MAPPER.readValue(path.toFile(), Court.class);
        final String searchSlug = "some-slug";

        when(courtService.getCourtBySlug(searchSlug)).thenReturn(court);

        mockMvc.perform(get(String.format(URL + "/%s", searchSlug)))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson))
            .andReturn();
    }

    @Test
    void shouldFindCourtsByPrefixAndDisplayed() throws Exception {
        final Path path = Paths.get("src/test/resources/courts.json");
        final String expectedJson = new String(readAllBytes(path));

        final List<CourtReference> courts = Arrays.asList(OBJECT_MAPPER.readValue(path.toFile(), CourtReference[].class));

        when(courtService.getCourtsByPrefixAndActiveSearch(anyString())).thenReturn(courts);
        mockMvc.perform(get(SEARCH_BY_PREFIX_AND_ACTIVE_URL))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson))
            .andReturn();
    }

    @Test
    void shouldFindCourtsByCourtTypes() throws Exception {

        final Path path = Paths.get("src/test/resources/full-court-model.json");
        final String expectedJson = new String(readAllBytes(path));
        final List<Court> courts = Arrays.asList(OBJECT_MAPPER.readValue(path.toFile(), Court[].class));

        when(courtService.getCourtsByCourtTypes(anyList())).thenReturn(courts);

        mockMvc.perform(get(URL + SEARCH_BY_COURT_TYPES))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson))
            .andReturn();
    }

    @Test
    void shouldFindCourtByHistoricalName() throws Exception {

        uk.gov.hmcts.dts.fact.entity.Court currentCourt = new uk.gov.hmcts.dts.fact.entity.Court();
        currentCourt.setName("currentCourtName");
        currentCourt.setDisplayed(true);
        CourtReferenceWithHistoricalName courtInfo = new CourtReferenceWithHistoricalName(
            currentCourt,
            new CourtHistory(
                3, 11, "oldCourtName", LocalDateTime.parse("2024-02-03T10:15:30"),
                LocalDateTime.parse("2023-12-03T11:15:30"), "")
        );

        final String courtInfoJson = OBJECT_MAPPER.writeValueAsString(courtInfo);

        when(courtService.getCourtByCourtHistoryName("fakeOldCourtName"))
            .thenReturn(Optional.of(courtInfo));

        mockMvc.perform(get(URL + SEARCH_BY_COURT_HISTORY_NAME + "?q=fakeOldCourtName"))
            .andExpect(status().isOk())
            .andExpect(content().json(courtInfoJson))
            .andReturn();
    }
}
