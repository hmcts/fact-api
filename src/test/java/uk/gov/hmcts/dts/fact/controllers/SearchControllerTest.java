package uk.gov.hmcts.dts.fact.controllers;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.services.CourtService;
import uk.gov.hmcts.dts.fact.util.Action;

import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("PMD.TooManyMethods")
@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    private static final String BASE_URL = "/search";
    private static final String POSTCODE = "OX1 1RZ";
    private static final String CRIME = "Crime";
    private static final String CHILDREN = "Children";
    private static final List<String> CHILDREN_AS_LIST = List.of("Children");

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

        verify(courtService).getNearestCourtsByPostcode(POSTCODE);
    }

    @Test
    void shouldSearchByPostcodeAndAreaOfLaw() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json?postcode=OX1 1RZ&aol=Crime"))
            .andExpect(status().isOk());

        verify(courtService).getNearestCourtsByPostcodeAndAreaOfLaw(POSTCODE, CRIME, true);
    }

    @Test
    void shouldSearchByPostcodeAndAreaOfLawAndLocalAuthority() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json?postcode=BN21 2BH&aol=Children"))
            .andExpect(status().isOk());

        verify(courtService).getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority("BN21 2BH", CHILDREN, true);
    }

    @Test
    void shouldSearchByNameOrAddress() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results.json?q=name"))
            .andExpect(status().isOk());

        verify(courtService).getCourtsByNameOrAddressOrPostcodeOrTown("name", true);
    }

    @Test
    void shouldSearchCourtsByPostcodeAndServiceArea() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results?postcode=OX1 1RZ&serviceArea=Crime&action="))
            .andExpect(status().isOk());

        verify(courtService).getNearestCourtsByPostcodeSearch(POSTCODE, CRIME, false, Action.UNDEFINED);
    }

    @Test
    void shouldSearchCourtsByPostcodeAndChildrenServiceArea() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results?postcode=B1 1AA&serviceArea=childcare-arrangements&action="))
            .andExpect(status().isOk());

        verify(courtService).getNearestCourtsByAreaOfLawSinglePointOfEntry("B1 1AA", "childcare-arrangements", CHILDREN_AS_LIST, Action.UNDEFINED, false);
    }

    @Test
    void shouldSearchCourtsByPostcodeNearestActionAndServiceArea() throws Exception {
        mockMvc.perform(get(BASE_URL + "/results?postcode=OX1 1RZ&serviceArea=Crime&action=nearest"))
            .andExpect(status().isOk());

        verify(courtService).getNearestCourtsByPostcodeActionAndAreaOfLawSearch(POSTCODE, CRIME, Action.NEAREST, false);
    }

    @Test
    void shouldSearchCourtsByPostcodeOnly() throws Exception {
        mockMvc.perform(get(format("%s/%s", BASE_URL, "results/sw1a2by")))
            .andExpect(status().isOk());

        verify(courtService).getNearestCourtReferencesByPostcode("sw1a2by");
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

    @Test
    void shouldReturnInvalidPostCodeError() {
        try {
            mockMvc.perform(get(BASE_URL + "/results/abc123")).andReturn();
        } catch (Exception e) {
            assertThrows(ConstraintViolationException.class, () -> {
                throw e.getCause();
            });
            assertThat(e.getMessage())
                .containsPattern("findCourtsByPostcode.postcode: Provided postcode is not valid");
        }
        verifyNoInteractions(courtService);
    }
}
