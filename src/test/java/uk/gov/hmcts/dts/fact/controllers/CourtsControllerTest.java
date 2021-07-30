package uk.gov.hmcts.dts.fact.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.services.CourtService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static org.mockito.ArgumentMatchers.*;
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

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
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
}
