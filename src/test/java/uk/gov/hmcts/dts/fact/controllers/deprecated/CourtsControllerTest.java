package uk.gov.hmcts.dts.fact.controllers.deprecated;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.SlugNotFoundException;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.services.CourtService;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourtsController.class)
class CourtsControllerTest {

    protected static final String URL = "/courts/%s.json";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private CourtService courtService;


    @Test
    void findCourtBySlug() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        final Path path = Paths.get("src/integrationTest/resources/aylesbury-magistrates-court-and-family-court.json");
        final String s1 = new String(readAllBytes(path));

        Court court = mapper.readValue(path.toFile(), Court.class);

        final String searchSlug = "some-slug";

        when(courtService.getCourtBySlug(searchSlug)).thenReturn(court);
        mockMvc.perform(get(String.format(URL, searchSlug)))
            .andExpect(status().isOk()).andExpect(content().json(s1)).andReturn();
    }

    @Test
    void findCourtByNonExistentSlug() throws Exception {

        final String searchSlug = "some-slug";
        when(courtService.getCourtBySlug(searchSlug)).thenThrow(new SlugNotFoundException(searchSlug));

        mockMvc.perform(get(String.format(URL, searchSlug)))
            .andExpect(status().is(404)).andReturn();
    }
}
