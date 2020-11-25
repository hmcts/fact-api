package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfo;
import uk.gov.hmcts.dts.fact.services.AdminService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCourtsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourtsControllerTest {

    private static final String URL = "/courts";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    void shouldFindAllCourts() throws Exception {

        final Path path = Paths.get("src/test/resources/courts.json");
        final String expectedJson = new String(readAllBytes(path));

        final List<CourtReference> courts = asList(OBJECT_MAPPER.readValue(path.toFile(), CourtReference[].class));

        when(adminService.getAllCourts()).thenReturn(courts);
        mockMvc.perform(get(URL + "/all"))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson))
            .andReturn();
    }

    @Test
    void shouldFindCourtBySlug() throws Exception {

        final Path path = Paths.get("src/test/resources/birmingham-civil-and-family-justice-centre-general.json");
        final String expectedJson = new String(readAllBytes(path));

        final CourtGeneral court = OBJECT_MAPPER.readValue(path.toFile(), CourtGeneral.class);

        final String searchSlug = "some-slug";

        when(adminService.getCourtGeneralBySlug(searchSlug)).thenReturn(court);
        mockMvc.perform(get(String.format(URL + "/%s/general", searchSlug)))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson))
            .andReturn();
    }

    @Test
    void shouldUpdateGeneralCourtBySlug() throws Exception {

        final Path courtEntityPath = Paths.get("src/test/resources/full-birmingham-civil-and-family-justice-centre-entity.json");
        final Court court = OBJECT_MAPPER.readValue(courtEntityPath.toFile(), Court.class);

        final CourtGeneral courtGeneral = new CourtGeneral(
            "slug",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre Info",
            "Birmingham Civil and Family Justice Centre Info",
            true,
            "Birmingham Civil and Family Justice Centre Alert",
            "Birmingham Civil and Family Justice Centre Alert"
        );

        court.setInfo(courtGeneral.getInfo());
        court.setInfoCy(courtGeneral.getInfoCy());
        court.setAlert(courtGeneral.getAlert());
        court.setAlertCy(courtGeneral.getAlertCy());
        when(adminService.saveGeneral(any(), any())).thenReturn(new CourtGeneral(court));

        final String searchSlug = "some-slug";
        final String json = OBJECT_MAPPER.writeValueAsString(courtGeneral);

        mockMvc.perform(put(String.format(URL + "/%s/general", searchSlug))
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.urgent_message").value(courtGeneral.getAlert()))
            .andExpect(jsonPath("$.urgent_message_cy").value(courtGeneral.getAlertCy()))
            .andExpect(jsonPath("$.info").value(courtGeneral.getInfo()))
            .andExpect(jsonPath("$.info_cy").value(courtGeneral.getInfoCy()))
            .andReturn();

    }

    @Test
    void updateAll() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        CourtInfo courtInfo = new CourtInfo(
            "Birmingham Civil and Family Justice Info",
            "Birmingham Civil and Family Justice Info"
        );

        String json = mapper.writeValueAsString(courtInfo);

        mockMvc.perform(put(URL + "/all/info")
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    }

    void shouldReturnNotFoundForUnknownSlug() throws Exception {
        final String searchSlug = "some-slug";

        when(adminService.getCourtGeneralBySlug(searchSlug)).thenThrow(new NotFoundException("search criteria"));

        mockMvc.perform(get(String.format(URL + "/%s/general", searchSlug)))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Not found: search criteria"))
            .andReturn();
    }
}
