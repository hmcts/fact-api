package uk.gov.hmcts.dts.fact.controllers.deprecated;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.services.CourtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    private static final String BASE_URL = "/search/results.json";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    CourtService courtService;

    @Test
    void shouldReturn400ErrorIfNoPostcode() throws Exception {
        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSearchByPostcode() throws Exception {
        mockMvc.perform(get(BASE_URL + "?postcode=OX1 1RZ"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldSearchByPostcodeAndAreaOfLaw() throws Exception {
        mockMvc.perform(get(BASE_URL + "?postcode=OX1 1RZ&aol=Crime"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotImplementedForSearchByPostcodeAndAreaOfLawAndSpoe() throws Exception {
        mockMvc.perform(get(BASE_URL + "?postcode=OX1 1RZ&aol=Crime&spoe=Nearest"))
            .andExpect(status().isNotImplemented());
    }
    @Test
    void shouldReturnNotImplementedForSearchByName() throws Exception {
        mockMvc.perform(get(BASE_URL + "?q=name"))
            .andExpect(status().isNotImplemented());
    }
}
