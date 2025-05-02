package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtForDownload;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.model.admin.ImageFile;
import uk.gov.hmcts.dts.fact.model.admin.NewCourt;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.services.admin.AdminService;
import uk.gov.hmcts.dts.fact.util.MvcSecurityUtil;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.util.TestHelper.getResourceAsJson;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports"})
@WebMvcTest(AdminCourtsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourtsControllerTest {

    private static final String TEST_URL = "/courts";
    private static final String TEST_COURT_PHOTO_URL = "/%s/courtPhoto";
    private static final String TEST_COURT_FILE = "courts.json";
    private static final String TEST_COURT_PHOTO_NAME = "birmingham_district_probate_registry.jpg";
    private static final String TEST_GENERAL_FILE = "birmingham-civil-and-family-justice-centre-general.json";
    private static final String TEST_COURT_ENTITY_FILE = "full-birmingham-civil-and-family-justice-centre-entity.json";
    private static final String TEST_SEARCH_SLUG = "some-slug";
    private static final String TEST_USER = "mosh@cat.com";
    private static final String SEARCH_CRITERIA = "search criteria";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String JSON_NOT_FOUND_SEARCH_CRITERIA = String.format(MESSAGE, NOT_FOUND + SEARCH_CRITERIA);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private AdminCourtLockService adminCourtLockService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

    @Test
    void shouldFindAllCourts() throws Exception {

        final String expectedJson = getResourceAsJson(TEST_COURT_FILE);
        final List<CourtReference> courts = asList(OBJECT_MAPPER.readValue(expectedJson, CourtReference[].class));

        when(adminService.getAllCourtReferences()).thenReturn(courts);
        mockMvc.perform(get(TEST_URL + "/all"))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson))
            .andReturn();
    }

    @Test
    void shouldDeleteCourt() throws Exception {
        String testCourtName = "test court";
        String testSlugName = "test-court";
        NewCourt newCourt = new NewCourt();
        newCourt.setNewCourtName(testCourtName);
        mockMvc.perform(delete(TEST_URL + "/" + testSlugName)
                            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string("Court with slug: test-court has been deleted"))
            .andReturn();
        verify(adminService, atMostOnce()).deleteCourt(testSlugName);
    }

    @Test
    void shouldAddNewCourt() throws Exception {
        Court expectedCourt = new Court();
        String testCourtName = "test court";
        String testSlugName = "test-court";
        NewCourt newCourt = new NewCourt();
        newCourt.setNewCourtName(testCourtName);
        newCourt.setServiceCentre(false);
        when(adminService.addNewCourt(testCourtName, testSlugName, newCourt.getServiceCentre(),
                                      newCourt.getLon(), newCourt.getLat(), newCourt.getServiceAreas())).thenReturn(expectedCourt);
        mockMvc.perform(post(TEST_URL + "/")
                            .with(csrf())
                            .content(OBJECT_MAPPER.writeValueAsString(newCourt))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated())
            .andReturn();
        verify(adminService, atMostOnce()).addNewCourt(testCourtName, testSlugName,
                                                       newCourt.getServiceCentre(), newCourt.getLon(),
                                                       newCourt.getLat(), newCourt.getServiceAreas());
    }

    @Test
    void shouldAddNewCourtWithApostrophesAndHyphens() throws Exception {
        Court expectedCourt = new Court();
        String testCourtName = "test court''-";
        String testSlugName = "test-court''-name-";
        NewCourt newCourt = new NewCourt();
        newCourt.setNewCourtName(testCourtName);
        newCourt.setServiceCentre(true);
        when(adminService.addNewCourt(testCourtName, testSlugName, newCourt.getServiceCentre(),
                                      newCourt.getLon(), newCourt.getLat(), newCourt.getServiceAreas()))
            .thenReturn(expectedCourt);
        mockMvc.perform(post(TEST_URL + "/")
                            .with(csrf())
                            .content(OBJECT_MAPPER.writeValueAsString(newCourt))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated())
            .andReturn();
        verify(adminService, atMostOnce()).addNewCourt(testCourtName, testSlugName, newCourt.getServiceCentre(),
                                                       newCourt.getLon(), newCourt.getLat(), newCourt.getServiceAreas());
    }

    @Test
    void shouldNotAddNewCourtWithInvalidPostcode() {
        Court expectedCourt = new Court();
        NewCourt newCourt = new NewCourt();
        newCourt.setNewCourtName("test court%$-");
        newCourt.setServiceCentre(false);
        when(adminService.addNewCourt("test court%$-",
                                      "test-court%$-", newCourt.getServiceCentre(),
                                      newCourt.getLon(), newCourt.getLat(), newCourt.getServiceAreas())).thenReturn(expectedCourt);
        try {
            mockMvc.perform(post(TEST_URL + "/")
                                .content(OBJECT_MAPPER.writeValueAsString(newCourt))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE));
        } catch (Exception e) {
            assertThrows(ConstraintViolationException.class, () -> {
                throw e.getCause();
            });
            assertThat(e.getMessage())
                .containsPattern("Request processing failed; nested exception is "
                                     + "javax.validation.ConstraintViolationException: addNewCourt.newCourtName: "
                                     + "Court name is not valid, should only contain a combination of characters, "
                                     + "numbers, apostrophes or hyphens");
        }
        verifyNoInteractions(adminService);
    }

    @Test
    void shouldFindAllCourtsForDownload() throws Exception {
        CourtForDownload courtForDownload1 = new CourtForDownload();
        CourtForDownload courtForDownload2 = new CourtForDownload();

        final List<CourtForDownload> courtsForDownloads = asList(courtForDownload1, courtForDownload2);

        when(adminService.getAllCourtsForDownload()).thenReturn(courtsForDownloads);
        mockMvc.perform(get(TEST_URL + "/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andReturn();
    }

    @Test
    void shouldFindCourtBySlug() throws Exception {

        final String expectedJson = getResourceAsJson(TEST_GENERAL_FILE);
        final Court courtEntity = OBJECT_MAPPER.readValue(expectedJson, Court.class);

        when(adminService.getCourtBySlug(TEST_SEARCH_SLUG)).thenReturn(courtEntity);
        mockMvc.perform(get(String.format(TEST_URL + "/%s/general", TEST_SEARCH_SLUG)))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson))
            .andReturn();
    }

    @Test
    void shouldUpdateGeneralCourtBySlug() throws Exception {

        final uk.gov.hmcts.dts.fact.entity.Court courtEntity = OBJECT_MAPPER.readValue(
            getResourceAsJson(TEST_COURT_ENTITY_FILE),
            uk.gov.hmcts.dts.fact.entity.Court.class
        );

        final Court court = new Court(
            "slug",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre Info",
            "Birmingham Civil and Family Justice Centre Info",
            true,
            false,
            false,
            "Birmingham Civil and Family Justice Centre Alert",
            "Birmingham Civil and Family Justice Centre Alert",
            emptyList(),
            emptyList(),
            false
        );

        courtEntity.setInfo(court.getInfo());
        courtEntity.setInfoCy(court.getInfoCy());
        courtEntity.setAlert(court.getAlert());
        courtEntity.setAlertCy(court.getAlertCy());
        when(adminService.save(any(), any())).thenReturn(new Court(courtEntity));

        final String json = OBJECT_MAPPER.writeValueAsString(court);

        mockMvc.perform(put(String.format(TEST_URL + "/%s/general", TEST_SEARCH_SLUG))
                            .with(csrf())
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.urgent_message").value(court.getAlert()))
            .andExpect(jsonPath("$.urgent_message_cy").value(court.getAlertCy()))
            .andExpect(jsonPath("$.info").value(court.getInfo()))
            .andExpect(jsonPath("$.info_cy").value(court.getInfoCy()))
            .andReturn();

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SEARCH_SLUG, TEST_USER);
    }

    @Test
    void updateCourtsInfo() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        CourtInfoUpdate courtInfo = new CourtInfoUpdate(
            Collections.singletonList("birmingham-civil-and-family-justice-centre-general"),
            "Birmingham Civil and Family Justice Info",
            "Birmingham Civil and Family Justice Info"
        );

        String json = mapper.writeValueAsString(courtInfo);

        mockMvc.perform(put(TEST_URL + "/info")
                            .with(csrf())
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    }

    @Test
    void shouldReturnNotFoundForUnknownSlug() throws Exception {
        final String searchSlug = TEST_SEARCH_SLUG;

        when(adminService.getCourtBySlug(searchSlug)).thenThrow(new NotFoundException(SEARCH_CRITERIA));

        mockMvc.perform(get(String.format(TEST_URL + "/%s/general", searchSlug)))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_SEARCH_CRITERIA))
            .andReturn();
    }

    @Test
    void shouldReturnCourtImageFile() throws Exception {

        when(adminService.getCourtImage(TEST_SEARCH_SLUG)).thenReturn(TEST_COURT_PHOTO_NAME);
        mockMvc.perform(get(String.format(TEST_URL + TEST_COURT_PHOTO_URL, TEST_SEARCH_SLUG)))
            .andExpect(status().isOk())
            .andExpect(content().string(TEST_COURT_PHOTO_NAME))
            .andReturn();
    }

    @Test
    void shouldNotReturnCourtImageFileForUnknownSlug() throws Exception {
        when(adminService.getCourtImage(TEST_SEARCH_SLUG)).thenThrow(new NotFoundException(SEARCH_CRITERIA));
        mockMvc.perform(get(String.format(TEST_URL + TEST_COURT_PHOTO_URL, TEST_SEARCH_SLUG)))
                            .andExpect(status().isNotFound())
                            .andExpect(content().json(JSON_NOT_FOUND_SEARCH_CRITERIA))
                            .andReturn();
    }

    @Test
    void shouldUpdateCourtImageFile() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        final ImageFile imageFile = new ImageFile();
        imageFile.setImageName(TEST_COURT_PHOTO_NAME);

        String json = mapper.writeValueAsString(imageFile);

        when(adminService.updateCourtImage(TEST_SEARCH_SLUG, imageFile.getImageName())).thenReturn(TEST_COURT_PHOTO_NAME);

        mockMvc.perform(put(String.format(TEST_URL + TEST_COURT_PHOTO_URL, TEST_SEARCH_SLUG))
                            .with(csrf())
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SEARCH_SLUG, TEST_USER);
    }

    @Test
    void shouldNotUpdateCourtImageFileForUnknownSlug() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        final ImageFile imageFile = new ImageFile();
        imageFile.setImageName(TEST_COURT_PHOTO_NAME);

        String json = mapper.writeValueAsString(imageFile);

        when(adminService.updateCourtImage(TEST_SEARCH_SLUG, imageFile.getImageName())).thenThrow(new NotFoundException(SEARCH_CRITERIA));
        mockMvc.perform(put(String.format(TEST_URL + TEST_COURT_PHOTO_URL, TEST_SEARCH_SLUG))
                            .with(csrf())
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_SEARCH_CRITERIA))
            .andReturn();

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SEARCH_SLUG, TEST_USER);
    }
}
