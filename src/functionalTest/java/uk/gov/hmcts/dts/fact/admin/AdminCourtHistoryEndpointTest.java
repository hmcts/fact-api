package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.CourtHistory;

import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.ADMIN_COURTS_ENDPOINT;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;


@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminCourtHistoryEndpointTest extends AdminFunctionalTestBase {
    
    private static final String COURT_HISTORY_PATH = ADMIN_COURTS_ENDPOINT + "history";
    private static final String COURT_HISTORY_ID_PATH = COURT_HISTORY_PATH + "/id";
    private static final String COURT_HISTORY_NAME_PATH = COURT_HISTORY_PATH + "/name";


    private static final int TEST_ID = 1;
    private static final int TEST_SEARCH_COURT_ID = 11;
    private static final String TEST_COURT_NAME = "TestCourt";
    private static final LocalDateTime TEST_DATE_UPDATED_AT = LocalDateTime.parse("2024-02-03T10:15:30");
    private static final LocalDateTime TEST_DATE_CREATED_AT = LocalDateTime.parse("2023-02-03T10:15:30");

//
//    @BeforeEach
//    void setUp() {
//        System.out.println("before each scenario");
//
//    }
//    @AfterEach
//    void tearDown() {
//        System.out.println("after each scenario");
//
//    }

//    @Test
//    @Order(1)
//    void shouldGetAllCourtHistory() {
//        System.out.println("2nd test");
//        final var response = doGetRequest(COURT_HISTORY_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken));
//
//        System.out.println(response.statusCode());
//        assertThat(response.statusCode()).isEqualTo(OK.value());
//
//        final List<CourtHistory> courtHistory = response.body().jsonPath().getList(".", CourtHistory.class);
//        assertThat(courtHistory).hasSizeLessThanOrEqualTo(1);
//
//    }

    @Test
    @Order(1)
    void shouldGetAllCourtHistory() throws JsonProcessingException  {
        // Creating first court history
        final String newCourtHistoryJson1 = createCourtHistory();
        final var response1 = doPostRequest(
            COURT_HISTORY_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newCourtHistoryJson1
        );

        assertThat(response1.statusCode()).isEqualTo(CREATED.value());

        // creating 2nd court history
        final String newCourtHistoryJson2 = createCourtHistory();
        final var response2 = doPostRequest(
            COURT_HISTORY_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newCourtHistoryJson2
        );
        assertThat(response2.statusCode()).isEqualTo(CREATED.value());

        final var response = doGetRequest(COURT_HISTORY_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken));

        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtHistory> courtHistory = response.body().jsonPath().getList(".", CourtHistory.class);
        assertThat(courtHistory).hasSizeGreaterThanOrEqualTo(1);

        //get court history by court History id test

        final int courtHistoryId = courtHistory.get(0).getId();

//        System.out.println("courtHistoryId.............." + courtHistoryId);
//        System.out.println("courtHistoryId.............." + courtHistory.get(1).getId());

        final var responseHistoryId = doGetRequest(COURT_HISTORY_PATH + "/" + courtHistoryId,
                                          Map.of(AUTHORIZATION, BEARER + superAdminToken));

        System.out.println("*********************************************************");
        System.out.println("COURTHISTORYRESPONSEBODY.............." + responseHistoryId.body().prettyPrint());

        System.out.println("*********************************************************");
        assertThat(responseHistoryId.statusCode()).isEqualTo(OK.value());

        final List<CourtHistory> courtHistoryById = responseHistoryId.body().jsonPath().getList(".", CourtHistory.class);

        System.out.println("COURTHISTORYBYID.............." + courtHistoryById);
//        assertThat(courtHistoryById).extracting(CourtHistory::getId).isEqualTo(courtHistoryId);

//
//        //get court history by court id test
//
//        final int courtId = courtHistory.get(0).getSearchCourtId();
//
//
//        final var responseCourtId = doGetRequest(COURT_HISTORY_ID_PATH + "/" + courtId,
//                                                   Map.of(AUTHORIZATION, BEARER + superAdminToken));
//
//        assertThat(responseCourtId.statusCode()).isEqualTo(OK.value());
//        final List<CourtHistory> courtHistoryBycourtId = responseCourtId.body().jsonPath().getList(".", CourtHistory.class);
//        System.out.println("courtsearchId.............." + courtHistoryBycourtId);

//        //get court history by court name
//
//        final int courtName = courtHistory.get(0).getSearchCourtId();
//
//        final var responseCourtName = doGetRequest(COURT_HISTORY_NAME_PATH + "/" + courtName,
//                                                 Map.of(AUTHORIZATION, BEARER + superAdminToken));
//
//        assertThat(responseCourtName.statusCode()).isEqualTo(OK.value());


    }


    @Test
    @Order(2)

    void shouldCreateCourtHistory() throws JsonProcessingException {
        System.out.println("first test");
        final String newCourtHistoryJson = createCourtHistory();

        final var response = doPostRequest(
            COURT_HISTORY_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newCourtHistoryJson
        );


        assertThat(response.statusCode()).isEqualTo(CREATED.value());


    }



//    void shouldCreateCourtHistory() throws JsonProcessingException {
//        System.out.println("first test");
//
//        final ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        //final List<CourtHistory> expectedCourtHistory = createCourtHistory();
//        final CourtHistory expectedCourtHistory = createCourtHistory();
//        final String newCourtHistoryJson = mapper.writeValueAsString(expectedCourtHistory);
//
//        System.out.println(newCourtHistoryJson);
//
//
////        final CourtHistory expectedCourtHistory = createCourtHistory();
////        final String newCourtHistoryJson = mapper.writeValueAsString(expectedCourtHistory);
//
//        final var response = doPostRequest(
//            COURT_HISTORY_PATH,
//            Map.of(AUTHORIZATION, BEARER + superAdminToken),
//            newCourtHistoryJson
//        );
//        assertThat(response.statusCode()).isEqualTo(CREATED.value());
//
//    }


        /************************************************************* Shared utility methods. ***************************************************************/

//    private CourtHistory createCourtHistory() {
//
//        final ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        final CourtHistory expectedCourtHistory = createCourtHistory();
//        final String newCourtHistoryJson = mapper.writeValueAsString(expectedCourtHistory);
//
//
//        return new CourtHistory(null, 1479944, "fakeCourt1", LocalDateTime.parse("2024-02-03T10:15:30"),
//                                LocalDateTime.parse("2007-12-03T10:15:30"), "test-cy");
//
//    }
    private static String createCourtHistory() throws JsonProcessingException {

        final CourtHistory courtHistory = new CourtHistory(null, 1479944, "fakeCourt1",
                                                                  LocalDateTime.parse("2024-02-03T10:15:30"),
                                                                  LocalDateTime.parse("2007-12-03T10:15:30"), "test-cy");

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper.writeValueAsString(courtHistory);
    }


    //    private List<CourtHistory> createCourtHistory() {
//
//        List<CourtHistory> FAKE_COURT_HISTORIES = Arrays.asList(
//            new CourtHistory(
//                null, 1479944, "fakeCourt1", LocalDateTime.parse("2024-02-03T10:15:30"),
//                LocalDateTime.parse("2007-12-03T10:15:30"), null),
//            new CourtHistory(
//                null, 1479944, "fakeCourt1", null, null, null),
//            new CourtHistory(
//                null, 1479944, "fakeCourt1", LocalDateTime.parse("2024-02-03T10:15:30"),
//                LocalDateTime.parse("2023-12-03T11:15:30"), ""),
//            new CourtHistory(
//                null, 1479944,"fakeCourt1", LocalDateTime.parse("2024-02-03T10:15:30"),
//                LocalDateTime.parse("2024-03-03T10:15:30"), "Llys y Goron") );
//
//        return FAKE_COURT_HISTORIES;
//    }



    }
