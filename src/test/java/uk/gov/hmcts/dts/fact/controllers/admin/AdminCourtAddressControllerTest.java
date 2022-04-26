package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAddressService;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCourtAddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtAddressControllerTest {
    private static final String TEST_SLUG = "court-slug";
    private static final String BASE_PATH = "/admin/courts/";
    private static final String ADDRESSES_PATH = "/" + "addresses";

    private static final List<String> ADDRESS1 = Arrays.asList("first address line 1", "first address line 2");
    private static final List<String> ADDRESS2 = Arrays.asList("second address line 1", "second address line 2");
    private static final String TOWN_NAME1 = "first town";
    private static final String TOWN_NAME2 = "second town";
    private static final String POSTCODE1 = "first postcode";
    private static final String POSTCODE2 = "second postcode";
    private static final String DESCRIPTION = "Description";
    private static final String DESCRIPTION_CY = "Description cy";


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final List<CourtAddress> COURT_ADDRESSES = Arrays.asList(
        new CourtAddress(1, ADDRESS1, null, TOWN_NAME1, null, POSTCODE1, DESCRIPTION, DESCRIPTION_CY),
        new CourtAddress(2, ADDRESS2, null, TOWN_NAME2, null, POSTCODE2, DESCRIPTION, DESCRIPTION_CY)
    );
    private static String courtAddressesJson;

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtAddressService adminService;

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        courtAddressesJson = OBJECT_MAPPER.writeValueAsString(COURT_ADDRESSES);
    }

    @Test
    void shouldRetrieveCourtAddresses() throws Exception {
        when(adminService.getCourtAddressesBySlug(TEST_SLUG)).thenReturn(COURT_ADDRESSES);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + ADDRESSES_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(courtAddressesJson));
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingAddressesForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtAddressesBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + ADDRESSES_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Not found: " + TEST_SLUG));
    }

    @Test
    void shouldUpdateCourtAddresses() throws Exception {
        when(adminService.validateCourtAddressPostcodes(TEST_SLUG, COURT_ADDRESSES)).thenReturn(emptyList());
        when(adminService.updateCourtAddressesAndCoordinates(TEST_SLUG, COURT_ADDRESSES)).thenReturn(COURT_ADDRESSES);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + ADDRESSES_PATH)
                            .content(courtAddressesJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(courtAddressesJson));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingAddressesForUnknownCourtSlug() throws Exception {
        when(adminService.validateCourtAddressPostcodes(TEST_SLUG, COURT_ADDRESSES)).thenReturn(emptyList());
        when(adminService.updateCourtAddressesAndCoordinates(TEST_SLUG, COURT_ADDRESSES)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + ADDRESSES_PATH)
                            .content(courtAddressesJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Not found: " + TEST_SLUG));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingAddressesWithAnInvalidPostcode() throws Exception {
        final List<String> expectedResult = singletonList(POSTCODE2);
        final String expectedResultJson = OBJECT_MAPPER.writeValueAsString(expectedResult);
        when(adminService.validateCourtAddressPostcodes(TEST_SLUG, COURT_ADDRESSES)).thenReturn(singletonList(POSTCODE2));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + ADDRESSES_PATH)
                            .content(courtAddressesJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(expectedResultJson));

        verify(adminService, never()).updateCourtAddressesAndCoordinates(TEST_SLUG, COURT_ADDRESSES);
    }
}
