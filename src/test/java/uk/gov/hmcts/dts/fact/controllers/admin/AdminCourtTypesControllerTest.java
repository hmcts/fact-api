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
import uk.gov.hmcts.dts.fact.model.OpeningTime;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtTypesService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCourtTypesController.class)
@AutoConfigureMockMvc(addFilters = false)

public class AdminCourtTypesControllerTest {


    private static final String BASE_PATH = "/admin/courts/";
    private static final String CHILD_PATH = "/courtTypes";
    private static final String CHILD_PATH_ALL = "courtTypes/all";
    private static final String TEST_SLUG = "unknownSlug";
    private static final Path TEST_COURT_TYPES_PATH = Paths.get("src/test/resources/court-types.json");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtTypesService adminService;



    @Test
    void getAllCourtTypesShouldReturnAllCourtTypes() throws Exception {
        final String expectedJson = getCourtTypesJson();
        final List<CourtType> courtTypes = asList(OBJECT_MAPPER.readValue(expectedJson, CourtType[].class));

        when(adminService.getAllCourtTypes()).thenReturn(courtTypes);

        mockMvc.perform(get(BASE_PATH + CHILD_PATH_ALL))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }



    @Test
    void getCourtCourtTypesShouldReturnCourtCourtTypes() throws Exception {
        final String expectedJson = getCourtTypesJson();
        final List<CourtType> courtTypes = asList(OBJECT_MAPPER.readValue(expectedJson, CourtType[].class));

        when(adminService.getCourtCourtTypesBySlug(TEST_SLUG)).thenReturn(courtTypes);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + CHILD_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void retrieveCourtCourtTypesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtCourtTypesBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + CHILD_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Not found: " + TEST_SLUG));
    }


    @Test
    void updateCourtCourtTypesShouldReturnUpdatedCourtCourtTypes() throws Exception {
        final String expectedJson = getCourtTypesJson();
        final List<CourtType> courtTypes = asList(OBJECT_MAPPER.readValue(expectedJson, CourtType[].class));

        when(adminService.updateCourtCourtTypes(TEST_SLUG, courtTypes)).thenReturn(courtTypes);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void updateCourtCourtTypesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        final String json = getCourtTypesJson();
        final List<CourtType> courtTypes = asList(OBJECT_MAPPER.readValue(json, CourtType[].class));

        when(adminService.updateCourtCourtTypes(TEST_SLUG, courtTypes)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CHILD_PATH)
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Not found: " + TEST_SLUG));
    }

    private static String getCourtTypesJson() throws IOException {
        return new String(readAllBytes(TEST_COURT_TYPES_PATH));
    }



}
