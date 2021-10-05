package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.CourtForDownload;
import uk.gov.hmcts.dts.fact.model.OpeningTime;
import uk.gov.hmcts.dts.fact.model.admin.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith({SpringExtension.class})
@SuppressWarnings("PMD.TooManyMethods")
public class AdminCourtsEndpointTest extends AdminFunctionalTestBase {

    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE = "Birmingham Civil and Family Justice Centre";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG = "birmingham-civil-and-family-justice-centre";
    private static final String TEST_STRING = "test";
    private static final String COURT_GENERAL_ENDPOINT = "/general";
    private static final String COURT_PHOTO_ENDPOINT = "/courtPhoto";
    private static final String BIRMINGHAM_COURT_PHOTO_PATH = COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_PHOTO_ENDPOINT;
    private static final String COURT_NOT_FIND_PATH = COURTS_ENDPOINT + "Birmingham-Centre" + COURT_PHOTO_ENDPOINT;

    @Test
    public void shouldRetrieveCourtsForDownload() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(COURTS_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<CourtForDownload> courts = asList(response.getBody().as(CourtForDownload[].class));
        assertThat(courts.size()).isGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenForAllCourtsForDownload() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURTS_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldRetrieveAllCourts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get("/courts/all")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        String slug = response.jsonPath().get("[0].slug");
        assertThat(slug).isEqualTo("greenwich-magistrates-court");
    }

    @Test
    public void shouldRequireATokenForAllCourts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/courts/all")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForAllCourts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get("/courts/all")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRetrieveCourtBySlug() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final Court court = response.as(Court.class);
        assertThat(court.getName()).isEqualTo(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE);
    }

    @Test
    public void shouldBeForbiddenFromGettingCourtBySlug() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldUpdateCourtBySlugAsAdmin() throws Exception {
        final Court courtUpdate = new Court(
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            "Admin Info",
            "Welsh Admin Info",
            true,
            false,
            false,
            "Admin Alert",
            "Welsh Admin Alert",
            openingTimes(),
            emptyList()
        );

        final String json = objectMapper().writeValueAsString(courtUpdate);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(json)
            .when()
            .put(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final Court court = response.as(Court.class);
        assertThat(court.getAlert()).isEqualTo(courtUpdate.getAlert());
        assertThat(court.getAlertCy()).isEqualTo(courtUpdate.getAlertCy());
        assertThat(court.getInfo()).isNotEqualTo(courtUpdate.getInfo());
        assertThat(court.getInfoCy()).isNotEqualTo(courtUpdate.getInfoCy());
    }

    @Test
    public void shouldBeForbiddenToUpdateCourtBySlug() throws Exception {
        final Court court = new Court(
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            "Admin Info",
            "Welsh Admin Info",
            true,
            false,
            false,
            "Admin Alert",
            "Welsh Admin Alert",
            emptyList(),
            emptyList()
        );

        final String json = objectMapper().writeValueAsString(court);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .body(json)
            .when()
            .put(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldUpdateCourtBySlugAsSuperAdmin() throws Exception {
        final Court courtUpdate = new Court(
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            "Super Admin Info",
            "Super Welsh Admin Info",
            true,
            false,
            false,
            "Super Admin Alert",
            "Super Welsh Admin Alert",
            openingTimes(),
            emptyList()
        );

        final String json = objectMapper().writeValueAsString(courtUpdate);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + superAdminToken)
            .body(json)
            .when()
            .put(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final Court courtResponse = response.as(Court.class);
        assertThat(courtResponse.getAlert()).isEqualTo(courtUpdate.getAlert());
        assertThat(courtResponse.getAlertCy()).isEqualTo(courtUpdate.getAlertCy());
        assertThat(courtResponse.getInfo()).isEqualTo(courtUpdate.getInfo());
        assertThat(courtResponse.getInfoCy()).isEqualTo(courtUpdate.getInfoCy());
    }

    @Test
    public void shouldNotUpdateCourtAsNoTokenProvided() throws Exception {
        final Court courtUpdate = new Court(
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            "Admin Info",
            "Welsh Admin Info",
            true,
            false,
            false,
            "Admin Alert",
            "Welsh Admin Alert",
            emptyList(),
            emptyList()
        );
        final String json = objectMapper().writeValueAsString(courtUpdate);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .body(json)
            .when()
            .put(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldUpdateCourts() throws Exception {
        CourtInfoUpdate courtInfo = new CourtInfoUpdate(
            Lists.newArrayList(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG),
            "Bulk info updated",
            "Bulk info updated Cy"
        );

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(courtInfo);
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + superAdminToken)
            .body(json)
            .when()
            .put("/courts/info")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());

        final var getResponse = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + superAdminToken)
            .when()
            .get(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(getResponse.statusCode()).isEqualTo(OK.value());
        final Court court = getResponse.as(Court.class);
        assertThat(court.getInfo()).isEqualTo(courtInfo.getInfo());
        assertThat(court.getInfoCy()).isEqualTo(courtInfo.getInfoCy());
    }
    /************************************************************* court photo GET request tests section. ***************************************************************/

    @Test
    public void shouldRetrieveCourtPhoto() {
        final var response = doGetRequest(
            BIRMINGHAM_COURT_PHOTO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final String courtPhoto = response.getBody().asString();
        assertThat(courtPhoto).isNotNull();
        assertThat(courtPhoto).isEqualTo("birmingham_civil_justice_centre_and_family_courts.jpg");

    }

    @Test
    public void shouldNotRetrieveCourtPhotoWhenCourtSlugNotFound() {
        final var response = doGetRequest(COURT_NOT_FIND_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void shouldRequireATokenWhenRetrievingCourtPhoto() {
        final var response = doGetRequest(BIRMINGHAM_COURT_PHOTO_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForRetrievingCourtPhoto() {
        final var response = doGetRequest(
            BIRMINGHAM_COURT_PHOTO_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* court photo PUT request tests section. ***************************************************************/

    @Test
    public void shouldUpdateCourtPhoto() {
        final var response = doGetRequest(
            BIRMINGHAM_COURT_PHOTO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        final String originalCourtPhotoString = response.getBody().asString();

        final var updateResponse = doPutRequest(
            BIRMINGHAM_COURT_PHOTO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            TEST_STRING);

        assertThat(updateResponse.statusCode()).isEqualTo(OK.value());
        final String courtPhoto = updateResponse.getBody().asString();
        assertThat(courtPhoto).isNotNull();
        assertThat(courtPhoto).isEqualTo(TEST_STRING);

        //cleanup

        final var responseTestClean = doPutRequest(
            BIRMINGHAM_COURT_PHOTO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            originalCourtPhotoString);
        assertThat(responseTestClean.statusCode()).isEqualTo(OK.value());
        final String originalString = responseTestClean.getBody().asString();
        assertThat(originalString).isNotNull();
        assertThat(originalString).isEqualTo(originalCourtPhotoString);
    }

    @Test
    public void shouldBeForbiddenForUpdatingCourtPhoto() {
        final Response response = doPutRequest(
            BIRMINGHAM_COURT_PHOTO_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), TEST_STRING
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenUpdatingCourtPhoto() {
        final Response response = doPutRequest(
            BIRMINGHAM_COURT_PHOTO_PATH, TEST_STRING
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotFoundForPhotoUpdateWhenCourtDoesNotExist() {

        final Response response = doPutRequest(
            COURT_NOT_FIND_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken),
            TEST_STRING
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    private List<OpeningTime> openingTimes() {
        OpeningTime openingTime1 = new OpeningTime();
        openingTime1.setType("Court open");
        openingTime1.setHours("Monday to Friday 9am to 5pm");
        OpeningTime openingTime2 = new OpeningTime();
        openingTime2.setType("Counter service by appointment only");
        openingTime2.setHours("By prior appointment only (except High Court and Administrative Court 10am to 4pm)");
        OpeningTime openingTime3 = new OpeningTime();
        openingTime3.setType("Telephone enquiries answered");
        openingTime3.setHours("8:30am to 5pm");
        return asList(openingTime1, openingTime2, openingTime3);
    }
}
