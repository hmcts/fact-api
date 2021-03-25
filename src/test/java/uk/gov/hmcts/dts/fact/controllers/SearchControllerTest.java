package uk.gov.hmcts.dts.fact.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.services.CourtService;

import static java.lang.String.format;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SearchController.class)
class SearchControllerTest {

    private static final String BASE_URL = "/search";

    @MockBean
    private CourtService courtService;

    @Autowired
    private transient MockMvc mockMvc;

    @Test
    void shouldReturnBadRequestErrorIfNoPostcode() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(courtService);
    }

    @Test
    void shouldSearchByPostcode() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json?postcode=OX1 1RZ"))
            .andExpect(status().isOk());

        verify(courtService).getNearestCourtsByPostcode("OX1 1RZ");
    }

    @Test
    void shouldSearchByPostcodeAndAreaOfLaw() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json?postcode=OX1 1RZ&aol=Crime"))
            .andExpect(status().isOk());

        verify(courtService).getNearestCourtsByPostcodeAndAreaOfLaw("OX1 1RZ", "Crime");
    }

    @Test
    void shouldSearchByPostcodeAndAreaOfLawAndLocalAuthority() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json?postcode=BN21 2BH&aol=Children"))
            .andExpect(status().isOk());

        verify(courtService).getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority("BN21 2BH", "Children");
    }

    @Test
    void shouldSearchByNameOrAddress() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json?q=name"))
            .andExpect(status().isOk());

        verify(courtService).getCourtsByNameOrAddressOrPostcodeOrTown("name");
    }

    @Test
    void shouldSearchCourtsByPostcodeAndServiceArea() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results?postcode=OX1 1RZ&serviceArea=Crime"))
            .andExpect(status().isOk());

        verify(courtService).getNearestCourtsByPostcodeSearch("OX1 1RZ", "Crime");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "serviceArea=Crime",
        "postcode=OX1 1RZ",
        ""
    })
    void shouldReturnBadRequestErrorForResultsRequests(final String requestParam) throws Exception {
        mockMvc.perform(get(BASE_URL + format("/results?%s", requestParam)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(courtService);
    }
}
