package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Facility;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtFacilityService;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.util.TestHelper.getResourceAsJson;

@WebMvcTest(AdminCourtFacilityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtFacilityControllerTest {

    private static final String BASE_PATH = "/admin/courts/";
    private static final String FACILITIES_PATH = "/facilities";
    private static final String FACILITY_PATH = "/facility";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String TEST_FACILITIES_PATH = "facilities.json";
    private static final String NOT_FOUND = "Not found: ";
    private static final String TEST_UNKNOWN_COURT_MESSAGE = "Court not found";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtFacilityService adminCourtFacilityService;

    @Test
    void shouldReturnCourtFacilities() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_FACILITIES_PATH);
        final List<Facility> facilities = asList(OBJECT_MAPPER.readValue(expectedJson, Facility[].class));

        when(adminCourtFacilityService.getCourtFacilitiesBySlug(TEST_SLUG)).thenReturn(facilities);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + FACILITIES_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void retrieveCourtFacilitiesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminCourtFacilityService.getCourtFacilitiesBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + FACILITIES_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().string(NOT_FOUND + TEST_SLUG));
    }

    @Test
    void updateCourtFacilityBySlugShouldReturnUpdatedCourtFacilities() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_FACILITIES_PATH);
        final List<Facility> facilities = asList(OBJECT_MAPPER.readValue(expectedJson, Facility[].class));


        when(adminCourtFacilityService.updateCourtFacility(TEST_SLUG, facilities)).thenReturn(facilities);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + FACILITY_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void updateCourtAuthorityShouldReturnBadRequestForUnknownCourtSlug() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_FACILITIES_PATH);
        final List<Facility> facilities = asList(OBJECT_MAPPER.readValue(expectedJson, Facility[].class));

        when(adminCourtFacilityService.updateCourtFacility(TEST_SLUG,facilities)).thenThrow(new IllegalArgumentException(
            TEST_UNKNOWN_COURT_MESSAGE));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + FACILITY_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(TEST_UNKNOWN_COURT_MESSAGE));
    }


}
