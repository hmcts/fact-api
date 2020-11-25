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
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfo;
import uk.gov.hmcts.dts.fact.services.AdminService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCourtsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourtsControllerTest {

    protected static final String URL = "/courts";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    void findAllCourts() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        final Path path = Paths.get("src/test/resources/courts.json");
        final String s1 = new String(readAllBytes(path));

        List<CourtReference> courts = Arrays.asList(mapper.readValue(path.toFile(), CourtReference[].class));

        when(adminService.getAllCourts()).thenReturn(courts);
        mockMvc.perform(get(String.format(URL + "/all")))
            .andExpect(status().isOk()).andExpect(content().json(s1)).andReturn();
    }

    @Test
    void findCourtBySlug() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        final Path path = Paths.get("src/test/resources/birmingham-civil-and-family-justice-centre-general.json");
        final String s1 = new String(readAllBytes(path));

        CourtGeneral court = mapper.readValue(path.toFile(), CourtGeneral.class);

        final String searchSlug = "some-slug";

        when(adminService.getCourtGeneralBySlug(searchSlug)).thenReturn(court);
        mockMvc.perform(get(String.format(URL + "/%s/general", searchSlug)))
            .andExpect(status().isOk()).andExpect(content().json(s1)).andReturn();
    }

    @Test
    void updateGeneralCourtBySlug() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final Path courtEntityPath = Paths.get("src/test/resources/full-birmingham-civil-and-family-justice-centre-entity.json");
        Court court = mapper.readValue(courtEntityPath.toFile(), Court.class);

        CourtGeneral courtGeneral = new CourtGeneral(
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
        String json = mapper.writeValueAsString(courtGeneral);

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
}
