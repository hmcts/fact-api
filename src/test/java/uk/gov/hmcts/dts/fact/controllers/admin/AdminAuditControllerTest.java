package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.entity.AuditType;
import uk.gov.hmcts.dts.fact.model.admin.Audit;
import uk.gov.hmcts.dts.fact.services.admin.AdminAuditService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAuditController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminAuditControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockitoBean
    private AdminAuditService adminAuditService;

    private static final int TEST_PAGE = 0;
    private static final int TEST_SIZE = 100;
    private static final String TEST_EMAIL = "mosh email";
    private static final String TEST_LOCATION = "kupo court";
    private static final LocalDateTime TEST_DATE_FROM =
        LocalDateTime.of(1000, 10, 10, 10, 10);
    private static final LocalDateTime TEST_DATE_TO =
        LocalDateTime.of(3000, 10, 10, 10, 10);
    private static final String BASE_PATH = "/admin/audit?page=%d&size=%d";
    private static final String BASE_PATH_SEARCH_NO_DATE_RANGE = BASE_PATH + "&location=%s&email=%s";
    private static final String BASE_PATH_SEARCH_WITH_DATE_RANGE = BASE_PATH + "&location=%s&email=%s&dateFrom=%s&dateTo=%s";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static String auditJson;

    private static final List<Audit> AUDIT_LIST = Arrays.asList(
        new Audit(new uk.gov.hmcts.dts.fact.entity.Audit(
            1, "kupo email", new AuditType(), "some data before",
            "some data after", "mosh court",
            LocalDateTime.of(1001, 1, 11, 11,11, 1)
        )),
        new Audit(new uk.gov.hmcts.dts.fact.entity.Audit(
            2, "kupo email 2", new AuditType(), "some data before 2",
            "some data after 2", "mosh court 2",
            LocalDateTime.of(1002, 2, 12, 12,12, 1)
        )),
        new Audit(new uk.gov.hmcts.dts.fact.entity.Audit(
            3, "kupo email 3", new AuditType(), "some data before 3",
            "some data after 3", "mosh court 3",
            LocalDateTime.of(1003, 3, 13, 13,13, 1)
        ))
    );

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        // If we do not include the register module here, the date will be outputted differently to
        // what we would expect, and will not match the json output from the controller call
        // based on how the spring framework deserializes it's requests etc
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        auditJson = OBJECT_MAPPER.writeValueAsString(AUDIT_LIST);
    }

    @Test
    void shouldRetrieveAuditsWithOnlyPageAndSize() throws Exception {

        when(adminAuditService.getAllAuditData(TEST_PAGE, TEST_SIZE, Optional.empty(), Optional.empty(),
                                               Optional.empty(), Optional.empty())).thenReturn(AUDIT_LIST);
        mockMvc.perform(get(String.format(BASE_PATH, TEST_PAGE, TEST_SIZE)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(auditJson));
    }

    @Test
    void shouldRetrieveAuditsWithPageSizeLocationAndEmail() throws Exception {

        when(adminAuditService.getAllAuditData(TEST_PAGE, TEST_SIZE, Optional.of(TEST_LOCATION), Optional.of(TEST_EMAIL),
                                               Optional.empty(), Optional.empty())).thenReturn(AUDIT_LIST);
        mockMvc.perform(get(String.format(BASE_PATH_SEARCH_NO_DATE_RANGE, TEST_PAGE,
                                          TEST_SIZE, TEST_LOCATION, TEST_EMAIL)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(auditJson));
    }

    @Test
    void shouldRetrieveAuditsWithPageSizeLocationEmailAndDateRange() throws Exception {

        when(adminAuditService.getAllAuditData(TEST_PAGE, TEST_SIZE, Optional.of(TEST_LOCATION),
                                               Optional.of(TEST_EMAIL), Optional.of(TEST_DATE_FROM),
                                               Optional.of(TEST_DATE_TO))).thenReturn(AUDIT_LIST);

        mockMvc.perform(get(String.format(BASE_PATH_SEARCH_WITH_DATE_RANGE, TEST_PAGE,
                                          TEST_SIZE, TEST_LOCATION, TEST_EMAIL,
                                          TEST_DATE_FROM, TEST_DATE_TO)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(auditJson));
    }

    @Test
    void shouldGetABadResponseIfOneDateSpecifiedButNotTheOther() throws Exception {

        when(adminAuditService.getAllAuditData(TEST_PAGE, TEST_SIZE, Optional.of(TEST_LOCATION),
                                               Optional.of(TEST_EMAIL), Optional.of(TEST_DATE_FROM),
                                               Optional.of(TEST_DATE_TO))).thenReturn(AUDIT_LIST);

        mockMvc.perform(get(String.format(BASE_PATH_SEARCH_WITH_DATE_RANGE, TEST_PAGE,
                                          TEST_SIZE, TEST_LOCATION, TEST_EMAIL,
                                          TEST_DATE_FROM, null)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnDatesInEuropeLondonTimezone() throws Exception {
        // Set up test data with a known ZonedDateTime
        ZonedDateTime dateTimeInLondon = LocalDateTime.of(2024, 11, 8, 13, 30, 10)
            .atZone(ZoneId.of("Europe/London"));

        Audit auditWithLondonTime = new Audit(new uk.gov.hmcts.dts.fact.entity.Audit(
            1, "kupo email", new AuditType(), "some data before",
            "some data after", "mosh court", dateTimeInLondon.toLocalDateTime()
        ));

        List<Audit> auditListWithLondonTime = List.of(auditWithLondonTime);
        String auditJsonWithLondonTime = OBJECT_MAPPER.writeValueAsString(auditListWithLondonTime);

        when(adminAuditService.getAllAuditData(TEST_PAGE, TEST_SIZE, Optional.empty(), Optional.empty(),
                                               Optional.empty(), Optional.empty())).thenReturn(auditListWithLondonTime);

        mockMvc.perform(get(String.format(BASE_PATH, TEST_PAGE, TEST_SIZE)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(auditJsonWithLondonTime));
    }

}
