package uk.gov.hmcts.dts.fact.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.SlugNotFoundException;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.services.CourtService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourtsController.class)
class CourtsControllerTest {

    protected static final String URL = "/courts";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private CourtService courtService;

    @Test
    void findCourtBySlugDeprecated() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        final Path path = Paths.get("src/integrationTest/resources/aylesbury-magistrates-court-and-family-court.json");
        final String s1 = new String(readAllBytes(path));

        Court court = mapper.readValue(path.toFile(), Court.class);

        final String searchSlug = "some-slug";

        when(courtService.getCourtBySlug(searchSlug)).thenReturn(court);
        mockMvc.perform(get(String.format(URL + "/%s.json", searchSlug)))
            .andExpect(status().isOk()).andExpect(content().json(s1)).andReturn();
    }

    @Test
    void findCourtByNonExistentSlug() throws Exception {

        final String searchSlug = "some-slug";
        when(courtService.getCourtBySlug(searchSlug)).thenThrow(new SlugNotFoundException(searchSlug));

        mockMvc.perform(get(String.format(URL + "/%s.json", searchSlug)))
            .andExpect(status().is(404)).andReturn();
    }

    @Test
    void findCourtByQuery() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        final Path path = Paths.get("src/integrationTest/resources/courts.json");
        final String s1 = new String(readAllBytes(path));

        List<CourtReference> courts = Arrays.asList(mapper.readValue(path.toFile(), CourtReference[].class));
        final String query = "london";

        when(courtService.getCourtByNameOrAddressOrPostcodeOrTown(query)).thenReturn(courts);
        mockMvc.perform(get(String.format(URL + "?q=" + query)))
            .andExpect(status().isOk()).andExpect(content().json(s1)).andReturn();
    }

    @Test
    void findCourtByEmptyQuery() throws Exception {
        List<CourtReference> courts = new ArrayList<>();
        final String query = "";

        when(courtService.getCourtByNameOrAddressOrPostcodeOrTown(query)).thenReturn(courts);
        mockMvc.perform(get(String.format(URL + "?q=" + query)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findCourtBySlug() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        final Path path = Paths.get("src/integrationTest/resources/aylesbury-magistrates-court-and-family-court.json");
        final String s1 = new String(readAllBytes(path));

        Court court = mapper.readValue(path.toFile(), Court.class);

        final String searchSlug = "some-slug";

        when(courtService.getCourtBySlug(searchSlug)).thenReturn(court);
        mockMvc.perform(get(String.format(URL + "/%s", searchSlug)))
            .andExpect(status().isOk()).andExpect(content().json(s1)).andReturn();
    }
}
