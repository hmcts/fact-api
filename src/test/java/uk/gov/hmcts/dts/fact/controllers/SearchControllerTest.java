package uk.gov.hmcts.dts.fact.controllers;

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

    private static final String BASE_URL = "/search";
    @MockBean
    CourtService courtService;
    @Autowired
    private transient MockMvc mockMvc;

    @Test
    void shouldReturn400ErrorIfNoPostcode() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSearchByPostcode() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json?postcode=OX1 1RZ"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldSearchByPostcodeAndAreaOfLaw() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json?postcode=OX1 1RZ&aol=Crime"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldSearchByNameOrAddress() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json?q=name"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldSearchCourtsByPostcodeAndAreaOfLaw() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results?postcode=OX1 1RZ&aol=Crime"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400ErrorIfNoPostcodeOrAreaOfLaw() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results"))
            .andExpect(status().isBadRequest());
    }
}
