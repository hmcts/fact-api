package uk.gov.hmcts.dts.fact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithHistoricalName;
import uk.gov.hmcts.dts.fact.model.admin.CourtHistory;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.dts.fact.util.TestUtil.ADMIN_COURTS_ENDPOINT;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;


@SuppressWarnings({"PMD.TooManyMethods", "PMD.UseUnderscoresInNumericLiterals"})
@ExtendWith({SpringExtension.class})
class CourtsEndpointTest extends AdminFunctionalTestBase {

    private static final String AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT
        = "aylesbury-magistrates-court-and-family-court";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE
        = "birmingham-civil-and-family-justice-centre";
    private static final String COURT_DETAIL_BY_SLUG_ENDPOINT = "/courts/";
    private static final String COURT_SEARCH_BY_PREFIX_AND_ACTIVE_ENDPOINT = "/courts/search";
    private static final String OLD_COURT_DETAIL_BY_SLUG_ENDPOINT = "/courts/%s.json";
    private static final String COURT_SEARCH_ENDPOINT = "/courts";
    private static final String COURT_SEARCH_BY_COURT_TYPES_ENDPOINT = "/courts/court-types/";

    protected static final String CARDIFF_SOCIAL_SECURITY_AND_CHILD_SUPPORT_TRIBUNAL = "cardiff-social-security-and-child-support-tribunal";

    private static final String COURT_SEARCH_BY_COURT_HISTORY_NAME = "/courts/court-history/";

    private static final String COURT_HISTORY_DELETE_PATH = ADMIN_COURTS_ENDPOINT +  "court-id/";


    private static final String COURT_HISTORY_PATH = ADMIN_COURTS_ENDPOINT + "history";

    private static final int TEST_SEARCH_COURT_ID = 1479944;
    private static final String  TEST_SEARCH_COURT_NAME = "fakeCourt1";



    @Test
    void shouldRetrieveCourtDetail() {
        final var response = doGetRequest(format(OLD_COURT_DETAIL_BY_SLUG_ENDPOINT, AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final OldCourt court = response.as(OldCourt.class);
        assertThat(court.getSlug()).isEqualTo(AYLESBURY_MAGISTRATES_COURT_AND_FAMILY_COURT);
    }

    @Test
    void shouldRetrieveCourtReferenceByPartialQuery() {
        final String name = "Oxford Combined Court Centre";
        final String slug = "oxford-combined-court-centre";

        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=Oxford Combine");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }



    @Test
    void shouldRetrieveCourtReferenceByFullQuery() {
        final String name = "Oxford Combined Court Centre";
        final String slug = "oxford-combined-court-centre";

        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=Oxford Combined Court Centre");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    void shouldRetrieveCourtReferenceWithTypoAndMissingPunctuation() {
        final String name = "Sheffield Magistrates' Court";
        final String slug = "sheffield-magistrates-court";

        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=Sheffid Magistrates");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    void shouldRetrieveCourtReferenceByPartialPostCodeQuery() {
        final String name = "Skipton County Court and Family Court";
        final String slug = "skipton-county-court-and-family-court";

        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=BD23");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    void shouldRetrieveCourtReferenceByFullPostCodeQuery() {
        final String name = "Skipton County Court and Family Court";
        final String slug = "skipton-county-court-and-family-court";

        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=BD23 1RH");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getName()).isEqualTo(name);
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    void shouldReturnBadRequestForEmptyQuery() {
        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=");
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    void shouldRetrieveCourtDetailBySlug() {
        final var response = doGetRequest(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final Court court = response.as(Court.class);
        assertThat(court.getSlug()).isEqualTo(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE);
    }

    @Test
    void shouldRetrieveCourtsByPrefixWhereDisplayedFalseAndCaseLower() {
        final var response = doGetRequest(COURT_SEARCH_BY_PREFIX_AND_ACTIVE_ENDPOINT + "?prefix=a&active=false");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courtReferences = response.body().jsonPath().getList(".", CourtReference.class);
        assertTrue(courtReferences.stream().allMatch(c -> c.getName().charAt(0) == 'A'));
        assertTrue(courtReferences.stream().allMatch(c -> c.getSlug().charAt(0) == 'a'));
    }

    @Test
    void shouldRetrieveCourtsByPrefixWhereDispayedTrueAndCaseUpper() {
        final var response = doGetRequest(COURT_SEARCH_BY_PREFIX_AND_ACTIVE_ENDPOINT + "?prefix=B&active=true");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courtReferences = response.body().jsonPath().getList(".", CourtReference.class);
        assertTrue(courtReferences.stream().allMatch(c -> c.getName().charAt(0) == 'B'));
        assertTrue(courtReferences.stream().allMatch(c -> c.getSlug().charAt(0) == 'b'));
    }

    @Test
    void shouldReturnAnErrorWhenSizeConstraintBreached() {
        final var response = doGetRequest(COURT_SEARCH_BY_PREFIX_AND_ACTIVE_ENDPOINT + "?prefix=mosh&active=true");
        assertThat(response.statusCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void shouldReturnAnErrorWhenRequiredParamMissing() {
        final var response = doGetRequest(COURT_SEARCH_BY_PREFIX_AND_ACTIVE_ENDPOINT + "?prefix=kupo");
        assertThat(response.statusCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void shouldRetrieveCourtDetailInWelsh() {
        final var response = doGetRequest(format(OLD_COURT_DETAIL_BY_SLUG_ENDPOINT, CARDIFF_SOCIAL_SECURITY_AND_CHILD_SUPPORT_TRIBUNAL),
                                          Map.of(ACCEPT_LANGUAGE, "cy"));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final OldCourt court = response.as(OldCourt.class);
        assertThat(court.getAddresses().get(0).getTownName()).isEqualTo("Caerdydd");
    }

    @Test
    void shouldRetrieveCourtDetailBySlugInWelsh() {
        final var response = doGetRequest(COURT_DETAIL_BY_SLUG_ENDPOINT + CARDIFF_SOCIAL_SECURITY_AND_CHILD_SUPPORT_TRIBUNAL,
                                          Map.of(ACCEPT_LANGUAGE, "cy"));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final Court court = response.as(Court.class);
        assertThat(court.getAddresses().get(0).getTownName()).isEqualTo("Caerdydd");
    }

    @Test
    void shouldNotRetrieveClosedCourts() {
        final String slug = "aylesbury-crown-court";
        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=aylesbury");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.get(0).getSlug()).isEqualTo(slug);
    }

    @Test
    void shouldNotReturnDuplicatesForCourtsWithMultipleAddresses() {
        final var response = doGetRequest(COURT_SEARCH_ENDPOINT + "?q=Darlington Magistrates' Court and Family Court");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtReference> courts = Arrays.asList(response.getBody().as(CourtReference[].class));
        assertThat(courts.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnCourtsByCourtTypes() {
        final var response = doGetRequest(COURT_SEARCH_BY_COURT_TYPES_ENDPOINT + "tribunal,family");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<Court> courts = Arrays.asList(response.getBody().as(Court[].class));
        assertTrue(courts.get(0).getCourtTypes()
                       .stream()
                       .anyMatch(type -> type.contains("Tribunal") || type.contains("Family Court")));
        assertTrue(courts.get(courts.size() - 1).getCourtTypes()
                       .stream()
                       .anyMatch(type -> type.contains("Tribunal") || type.contains("Family Court")));
    }

    @Test
    void shouldReturnNotFoundForEmptyCourtTypes() {
        final var response = doGetRequest(COURT_SEARCH_BY_COURT_TYPES_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    void shouldRetrieveCurrentCourtByHistoricalCourtName() throws JsonProcessingException {
        final String name = "Edinburgh Employment Tribunal";

        // Creating  court history
        Response response1 = createCourtHistory();
        assertThat(response1.statusCode()).isEqualTo(CREATED.value());

        final var response = doGetRequest(COURT_HISTORY_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtHistory> courtHistory = response.body().jsonPath().getList(".", CourtHistory.class);
        assertThat(courtHistory).hasSizeLessThanOrEqualTo(1);

        final var historyNameResponse = doGetRequest(COURT_SEARCH_BY_COURT_HISTORY_NAME + "search?q=fakeCourt1");
        assertThat(historyNameResponse.statusCode()).isEqualTo(OK.value());

        final CourtReferenceWithHistoricalName court = historyNameResponse.as(CourtReferenceWithHistoricalName.class);
        assertThat(court).extracting("name","historicalName").contains(name,"fakeCourt1");

        assertThat(court.getName()).isEqualTo(name);
        assertThat(court.getHistoricalName()).isEqualTo("fakeCourt1");

        cleanUp();
    }

    @Test
    void shouldReturnNoContentsForNonExistingCourtHistoricalName() throws JsonProcessingException {

        // Creating  court history
        Response response1 = createCourtHistory();
        assertThat(response1.statusCode()).isEqualTo(CREATED.value());

        final var response = doGetRequest(COURT_HISTORY_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final var historyNameResponse = doGetRequest(COURT_SEARCH_BY_COURT_HISTORY_NAME + "search?q=testCourt");
        assertThat(historyNameResponse.statusCode()).isEqualTo(204);

        cleanUp();
    }

    private Response createCourtHistory() throws JsonProcessingException {

        final CourtHistory courtHistory = new CourtHistory(null, TEST_SEARCH_COURT_ID, TEST_SEARCH_COURT_NAME,
                                                           LocalDateTime.parse("2024-02-03T10:15:30"),
                                                           LocalDateTime.parse("2007-12-03T10:15:30"), "court-name-cy");
        final ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String newCourtHistoryJson = mapper.writeValueAsString(courtHistory);

        return doPostRequest(
            COURT_HISTORY_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newCourtHistoryJson
        );

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
