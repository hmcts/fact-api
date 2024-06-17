package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.CourtHistory;

import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
@SuppressWarnings({"PMD.TooManyMethods", "PMD.UseUnderscoresInNumericLiterals", "PMD.TestClassWithoutTestCases"})
 class AdminCourtHistoryEndpointTest extends AdminFunctionalTestBase {
    private static final String COURT_HISTORY_PATH = ADMIN_COURTS_ENDPOINT + "history";
    private static final String COURT_HISTORY_SEARCH_COURT_NAME_PATH = ADMIN_COURTS_ENDPOINT + "name/";
    private static final String COURT_HISTORY_DELETE_PATH = ADMIN_COURTS_ENDPOINT +  "court-id/";
    private static final String COURT_NOT_FIND_PATH = ADMIN_COURTS_ENDPOINT + "court-not-exist/history";
    private static final String COURT_HISTORY_SLUG_PATH = ADMIN_COURTS_ENDPOINT + "edinburgh-employment-tribunal/history";
    private static final int TEST_SEARCH_COURT_ID = 1479944;
    private static final String  TEST_SEARCH_COURT_NAME = "fakeCourt1";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void initialise() {
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    // Get Request Tests

    @Test
    void shouldGetAllCourtHistory() throws JsonProcessingException {
        // Creating first court history
        Response response1 = createCourtHistory();

        assertThat(response1.statusCode()).isEqualTo(CREATED.value());

        // creating 2nd court history
        Response response2 = createCourtHistory();

        assertThat(response2.statusCode()).isEqualTo(CREATED.value());
        final var response = doGetRequest(COURT_HISTORY_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtHistory> courtHistory = response.body().jsonPath().getList(".", CourtHistory.class);
        assertThat(courtHistory).hasSizeGreaterThanOrEqualTo(1);

        // clean up step
        cleanUp();
    }

    @Test
    void shouldRequireATokenWhenGettingCourtHistory() {
        final var response = doGetRequest(COURT_HISTORY_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingCourtHistory() {
        final var response = doGetRequest(
            COURT_HISTORY_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldGetAllCourtHistoryBySlug() throws JsonProcessingException {
        // Creating  court history
        Response response1 = createCourtHistory();

        assertThat(response1.statusCode()).isEqualTo(CREATED.value());

        final var response = doGetRequest(COURT_HISTORY_SLUG_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken));

        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtHistory> courtHistory = response.body().jsonPath().getList(".", CourtHistory.class);
        assertThat(courtHistory).hasSizeGreaterThanOrEqualTo(1);

        // clean up step
        cleanUp();
    }

    @Test
    void shouldGetAllCourtHistoryByHistoryName() throws JsonProcessingException {
        // Creating  court history
        Response response1 = createCourtHistory();

        assertThat(response1.statusCode()).isEqualTo(CREATED.value());
        final var response = doGetRequest(COURT_HISTORY_SEARCH_COURT_NAME_PATH + TEST_SEARCH_COURT_NAME + "/history", Map.of(AUTHORIZATION, BEARER + superAdminToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtHistory> courtHistory = response.body().jsonPath().getList(".", CourtHistory.class);
        assertThat(courtHistory).hasSize(1).extracting("courtName").contains("fakeCourt1");

        // clean up step
        cleanUp();
    }


    // Update Request Tests.

    @Test
    void shouldUpdateCourtHistory() throws JsonProcessingException {
        // Creating  court history
        Response response = createCourtHistory();

        assertThat(response.statusCode()).isEqualTo(CREATED.value());

        final var getResponse = doGetRequest(COURT_HISTORY_SLUG_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken));

        assertThat(getResponse.statusCode()).isEqualTo(OK.value());

        final List<CourtHistory> currentCourtHistory = getResponse.body().jsonPath().getList(".", CourtHistory.class);
        assertThat(currentCourtHistory).hasSize(1).extracting("courtName").contains("fakeCourt1");
        assertThat(currentCourtHistory).hasSize(1).extracting("courtNameCy").contains("court-name-cy");

        currentCourtHistory.get(0).setCourtName("test-name");
        currentCourtHistory.get(0).setCourtNameCy("test-name-cy");

        String newCourtHistoryJson = MAPPER.writeValueAsString(currentCourtHistory);

        final Response updateResponse =  doPutRequest(
            COURT_HISTORY_SLUG_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newCourtHistoryJson
        );

        assertThat(response.statusCode()).isEqualTo(201);

        final List<CourtHistory> updatedCourtHistory = updateResponse.body().jsonPath().getList(".", CourtHistory.class);
        assertThat(updatedCourtHistory).hasSize(1).extracting("courtName").contains("test-name");
        assertThat(updatedCourtHistory).hasSize(1).extracting("courtNameCy").contains("test-name-cy");

        // clean up step
        cleanUp();
    }

    @Test
    void shouldNotUpdateCourtHistoryWhenCourtSlugNotFound() throws JsonProcessingException {
        final var response = doPutRequest(
            COURT_NOT_FIND_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            getTestCourtHistories()
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    void shouldRequireATokenWhenUpdatingCourtHistory() throws JsonProcessingException {
        final var response = doPutRequest(
            COURT_HISTORY_SLUG_PATH,
            getTestCourtHistories()
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForUpdatingCourtHistories() throws JsonProcessingException {
        final var response = doPutRequest(
            COURT_HISTORY_SLUG_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken),
            getTestCourtHistories()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }


    // Delete Request Tests

    @Test
    void adminShouldBeForbiddenForDeletingCourtHistory()  {

        final var response = doDeleteRequest(
            COURT_HISTORY_DELETE_PATH + TEST_SEARCH_COURT_ID + "/history",
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            ""
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenDeletingCourtHistory() {
        final Response response = doDeleteRequest(
            COURT_HISTORY_PATH + 1,
            ""
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotDeleteCourtHistoryNotFound() {

        final var response = doDeleteRequest(
            COURT_HISTORY_PATH + 1,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            ""
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }


    // Shared utility methods

    private Response createCourtHistory() throws JsonProcessingException {

        final CourtHistory courtHistory = new CourtHistory(null, TEST_SEARCH_COURT_ID, TEST_SEARCH_COURT_NAME,
                                                           LocalDateTime.parse("2024-02-03T10:15:30"),
                                                           LocalDateTime.parse("2007-12-03T10:15:30"), "court-name-cy");
        String newCourtHistoryJson = MAPPER.writeValueAsString(courtHistory);

        return doPostRequest(
            COURT_HISTORY_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newCourtHistoryJson
        );
    }

    private static String getTestCourtHistories() throws JsonProcessingException {
        final List<CourtHistory> courtHistory = Arrays.asList(
            new CourtHistory(null, TEST_SEARCH_COURT_ID, TEST_SEARCH_COURT_NAME,
                             LocalDateTime.parse("2024-02-03T10:15:30"),
                             LocalDateTime.parse("2007-12-03T10:15:30"), "court-name-cy")
        );
        return MAPPER.writeValueAsString(courtHistory);
    }

    private void cleanUp() {
        final var deleteResponse = doDeleteRequest(
            COURT_HISTORY_DELETE_PATH + TEST_SEARCH_COURT_ID + "/history",
            Map.of(AUTHORIZATION, BEARER + superAdminToken),""
        );

        assertThat(deleteResponse.statusCode()).isEqualTo(OK.value());
        final var cleanupResponse = doGetRequest(COURT_HISTORY_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken));
        assertThat(cleanupResponse.statusCode()).isEqualTo(OK.value());

        final List<CourtHistory> cleanCourtHistory = cleanupResponse.body().jsonPath().getList(".", CourtHistory.class);
        assertThat(cleanCourtHistory).isEmpty();
    }
}
