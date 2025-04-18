package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.CourtForDownload;
import uk.gov.hmcts.dts.fact.model.OpeningTime;
import uk.gov.hmcts.dts.fact.model.admin.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.model.admin.ImageFile;
import uk.gov.hmcts.dts.fact.model.admin.NewCourt;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.ADMIN_COURTS_ENDPOINT;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.COURTS_ENDPOINT;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith({SpringExtension.class})
@SuppressWarnings("PMD.TooManyMethods")
class AdminCourtsEndpointTest extends AdminFunctionalTestBase {

    private static final String CARDIFF_CROWN_COURT = "Cardiff Crown Court";
    private static final String CARDIFF_CROWN_COURT_SLUG = "cardiff-crown-court";
    private static final String TEST_STRING = "test";
    private static final String ALL_COURT_ENDPOINT = "/courts/all";
    private static final String COURT_GENERAL_ENDPOINT = "/general";
    private static final String COURT_PHOTO_ENDPOINT = "/courtPhoto";
    private static final String CARDIFF_COURT_PHOTO_PATH = COURTS_ENDPOINT + CARDIFF_CROWN_COURT_SLUG + COURT_PHOTO_ENDPOINT;
    private static final String COURT_NOT_FIND_PATH = COURTS_ENDPOINT + "Birmingham-Centre" + COURT_PHOTO_ENDPOINT;
    private static final String DELETE_LOCK_BY_EMAIL_PATH = ADMIN_COURTS_ENDPOINT + "hmcts.fact@gmail.com/lock";
    private static final double LONGITUDE = 100.02;
    private static final double LATITUDE = -100.02;
    private static final NewCourt EXPECTED_NEW_COURT = new NewCourt("new court1", true, asList(
        "Divorce",
        "Single Justice Procedure"
    ), LONGITUDE, LATITUDE);
    private static final String EXPECTED_NEW_SLUG = "new-court1";

    @Test
    void shouldRetrieveCourtsForDownload() {
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
    void shouldRequireATokenForAllCourtsForDownload() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURTS_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldRetrieveAllCourts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ALL_COURT_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        List<String> slugs = response.jsonPath().getList("slug");
        Assertions.assertTrue(slugs.contains("greenwich-magistrates-court"));
    }

    @Test
    void shouldRequireATokenForAllCourts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(ALL_COURT_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForAllCourts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(ALL_COURT_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRetrieveCourtBySlug() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(COURTS_ENDPOINT + CARDIFF_CROWN_COURT_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final Court court = response.as(Court.class);
        assertThat(court.getName()).isEqualTo(CARDIFF_CROWN_COURT);
    }

    @Test
    void shouldBeForbiddenFromGettingCourtBySlug() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(COURTS_ENDPOINT + CARDIFF_CROWN_COURT_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldUpdateCourtBySlugAsAdmin() throws Exception {
        final var delResponse = doDeleteRequest(DELETE_LOCK_BY_EMAIL_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
                                                "");
        assertThat(delResponse.statusCode()).isEqualTo(OK.value());
        final Court courtUpdate = new Court(
            CARDIFF_CROWN_COURT_SLUG,
            CARDIFF_CROWN_COURT,
            CARDIFF_CROWN_COURT,
            "Admin Info",
            "Welsh Admin Info",
            true,
            false,
            false,
            "Admin Alert",
            "Welsh Admin Alert",
            openingTimes(),
            emptyList(),
            false
        );

        final String json = objectMapper().writeValueAsString(courtUpdate);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(json)
            .when()
            .put(COURTS_ENDPOINT + CARDIFF_CROWN_COURT_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final Court court = response.as(Court.class);
        assertThat(court.getAlert()).isEqualTo(courtUpdate.getAlert());
        assertThat(court.getAlertCy()).isEqualTo(courtUpdate.getAlertCy());
        assertThat(court.getInfo()).isNotEqualTo(courtUpdate.getInfo());
        assertThat(court.getInfoCy()).isNotEqualTo(courtUpdate.getInfoCy());
    }

    @Test
    void shouldBeForbiddenToUpdateCourtBySlug() throws Exception {
        final Court court = new Court(
            CARDIFF_CROWN_COURT_SLUG,
            CARDIFF_CROWN_COURT,
            CARDIFF_CROWN_COURT,
            "Admin Info",
            "Welsh Admin Info",
            true,
            false,
            false,
            "Admin Alert",
            "Welsh Admin Alert",
            emptyList(),
            emptyList(),
            false
        );

        final String json = objectMapper().writeValueAsString(court);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .body(json)
            .when()
            .put(COURTS_ENDPOINT + CARDIFF_CROWN_COURT_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldUpdateCourtBySlugAsSuperAdmin() throws Exception {

        //calling user delete lock endpoint to remove lock for the court
        final var delResponse = doDeleteRequest(DELETE_LOCK_BY_EMAIL_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
                                                "");
        assertThat(delResponse.statusCode()).isEqualTo(OK.value());

        final Court courtUpdate = new Court(
            CARDIFF_CROWN_COURT_SLUG,
            CARDIFF_CROWN_COURT,
            CARDIFF_CROWN_COURT,
            "Super Admin Info",
            "Super Welsh Admin Info",
            true,
            false,
            false,
            "Super Admin Alert",
            "Super Welsh Admin Alert",
            openingTimes(),
            emptyList(),
            false
        );

        final String json = objectMapper().writeValueAsString(courtUpdate);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + superAdminToken)
            .body(json)
            .when()
            .put(COURTS_ENDPOINT + CARDIFF_CROWN_COURT_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final Court courtResponse = response.as(Court.class);
        assertThat(courtResponse.getAlert()).isEqualTo(courtUpdate.getAlert());
        assertThat(courtResponse.getAlertCy()).isEqualTo(courtUpdate.getAlertCy());
        assertThat(courtResponse.getInfo()).isEqualTo(courtUpdate.getInfo());
        assertThat(courtResponse.getInfoCy()).isEqualTo(courtUpdate.getInfoCy());
    }

    @Test
    void shouldNotUpdateCourtAsNoTokenProvided() throws Exception {
        final Court courtUpdate = new Court(
            CARDIFF_CROWN_COURT_SLUG,
            CARDIFF_CROWN_COURT,
            CARDIFF_CROWN_COURT,
            "Admin Info",
            "Welsh Admin Info",
            true,
            false,
            false,
            "Admin Alert",
            "Welsh Admin Alert",
            emptyList(),
            emptyList(),
            false
        );
        final String json = objectMapper().writeValueAsString(courtUpdate);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .body(json)
            .when()
            .put(COURTS_ENDPOINT + CARDIFF_CROWN_COURT_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldUpdateCourts() throws Exception {
        CourtInfoUpdate courtInfo = new CourtInfoUpdate(
            Lists.newArrayList(CARDIFF_CROWN_COURT_SLUG),
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
            .get(COURTS_ENDPOINT + CARDIFF_CROWN_COURT_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(getResponse.statusCode()).isEqualTo(OK.value());
        final Court court = getResponse.as(Court.class);
        assertThat(court.getInfo()).isEqualTo(courtInfo.getInfo());
        assertThat(court.getInfoCy()).isEqualTo(courtInfo.getInfoCy());
    }
    /************************************************************* court photo GET request tests section. ***************************************************************/

    @Test
    void shouldRetrieveCourtPhoto() {
        final var response = doGetRequest(
            CARDIFF_COURT_PHOTO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final String courtPhoto = response.getBody().asString();
        assertThat(courtPhoto).isNotNull();
        assertThat(courtPhoto).isEqualTo("cardiff_crown_court.jpg");

    }

    @Test
    void shouldNotRetrieveCourtPhotoWhenCourtSlugNotFound() {
        final var response = doGetRequest(COURT_NOT_FIND_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    void shouldRequireATokenWhenRetrievingCourtPhoto() {
        final var response = doGetRequest(CARDIFF_COURT_PHOTO_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForRetrievingCourtPhoto() {
        final var response = doGetRequest(
            CARDIFF_COURT_PHOTO_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* court photo PUT request tests section. ***************************************************************/

    @Test
    void shouldUpdateCourtPhoto() throws JsonProcessingException {
        final var response = doGetRequest(
            CARDIFF_COURT_PHOTO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        final String originalCourtPhotoString = response.getBody().asString();

        ImageFile imageFile = new ImageFile();
        imageFile.setImageName(TEST_STRING);
        final var updateResponse = doPutRequest(
            CARDIFF_COURT_PHOTO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            objectMapper().writeValueAsString(imageFile));

        assertThat(updateResponse.statusCode()).isEqualTo(OK.value());
        final String courtPhoto = updateResponse.getBody().asString();
        assertThat(courtPhoto).isNotNull();
        assertThat(courtPhoto).isEqualTo(TEST_STRING);

        //cleanup
        imageFile.setImageName(originalCourtPhotoString);
        final var responseTestClean = doPutRequest(
            CARDIFF_COURT_PHOTO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            objectMapper().writeValueAsString(imageFile));
        assertThat(responseTestClean.statusCode()).isEqualTo(OK.value());
        final String originalString = responseTestClean.getBody().asString();
        assertThat(originalString).isNotNull();
        assertThat(originalString).isEqualTo(originalCourtPhotoString);
    }

    @Test
    void shouldBeForbiddenForUpdatingCourtPhoto() throws JsonProcessingException {
        ImageFile imageFile = new ImageFile();
        imageFile.setImageName(TEST_STRING);
        final Response response = doPutRequest(
            CARDIFF_COURT_PHOTO_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), objectMapper().writeValueAsString(imageFile)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenUpdatingCourtPhoto() throws JsonProcessingException {
        ImageFile imageFile = new ImageFile();
        imageFile.setImageName(TEST_STRING);
        final Response response = doPutRequest(
            CARDIFF_COURT_PHOTO_PATH, objectMapper().writeValueAsString(imageFile)
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotFoundForPhotoUpdateWhenCourtDoesNotExist() throws JsonProcessingException {
        ImageFile imageFile = new ImageFile();
        imageFile.setImageName(TEST_STRING);
        final Response response = doPutRequest(
            COURT_NOT_FIND_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken),
            objectMapper().writeValueAsString(imageFile)
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* court POST request tests section. ***************************************************************/

    @Test
    void shouldCreateNewCourt() throws JsonProcessingException {
        EXPECTED_NEW_COURT.setNewCourtName("new court1");
        final String newCourtNameJson = objectMapper().writeValueAsString(EXPECTED_NEW_COURT);
        final var response = doPostRequest(
            COURTS_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newCourtNameJson
        );
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        final Court court = response.as(Court.class);
        assertThat(court.getName()).isEqualTo(EXPECTED_NEW_COURT.getNewCourtName());
        assertThat(court.getSlug()).isEqualTo(EXPECTED_NEW_SLUG);

        //clean up by removing added court
        final var cleanUpResponse = doDeleteRequest(
            COURTS_ENDPOINT  + EXPECTED_NEW_SLUG,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),newCourtNameJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());

        final var responseAfterClean = doGetRequest(
            ALL_COURT_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(responseAfterClean.statusCode()).isEqualTo(OK.value());

        List<String> slugs = responseAfterClean.jsonPath().getList("slug");
        assertThat(slugs).doesNotContain(EXPECTED_NEW_SLUG);
    }


    @Test
    void shouldNotCreateCourtThatAlreadyExist() throws JsonProcessingException {
        EXPECTED_NEW_COURT.setNewCourtName("aldershot Justice Centre");
        final String testJson = objectMapper().writeValueAsString(EXPECTED_NEW_COURT);
        final Response response = doPostRequest(
            COURTS_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    @Test
    void adminShouldBeForbiddenToCreateCourt() throws JsonProcessingException {
        final String testJson = objectMapper().writeValueAsString(EXPECTED_NEW_COURT);
        final Response response = doPostRequest(
            COURTS_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenCreatingNewCourt() throws JsonProcessingException {
        final String testJson = objectMapper().writeValueAsString(EXPECTED_NEW_COURT);
        final Response response = doPostRequest(
            COURTS_ENDPOINT, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    /************************************************************* Delete request tests section. ***************************************************************/

    @Test
    void adminShouldBeForbiddenForDeletingCourt() {

        final var response = doDeleteRequest(
            COURTS_ENDPOINT + EXPECTED_NEW_SLUG,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), ""
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenDeletingOpeningCourt() {
        final var response = doDeleteRequest(
            COURTS_ENDPOINT + EXPECTED_NEW_SLUG,
            ""
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
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
