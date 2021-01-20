package uk.gov.hmcts.dts.fact;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.CourtForDownload;
import uk.gov.hmcts.dts.fact.model.admin.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {OAuthClient.class})
public class AdminCourtsEndpointTest {

    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE
        = "Birmingham Civil and Family Justice Centre";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG
        = "birmingham-civil-and-family-justice-centre";
    private static final String CONTENT_TYPE_VALUE = "application/json";
    private static final String COURTS_ENDPOINT = "/courts/";
    private static final String COURT_GENERAL_ENDPOINT = "/general";
    private static final String BEARER = "Bearer ";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @Autowired
    private OAuthClient authClient;

    private String authenticatedToken;
    private String forbiddenToken;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        authenticatedToken = authClient.getToken();
        forbiddenToken = authClient.getNobodyToken();
    }

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
        final List<CourtForDownload> courts = Arrays.asList(response.getBody().as(CourtForDownload[].class));
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
            emptyList()
        );

        final String json = OBJECT_MAPPER.writeValueAsString(courtUpdate);

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
            emptyList()
        );

        final String json = OBJECT_MAPPER.writeValueAsString(court);

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
            emptyList()
        );

        final String json = OBJECT_MAPPER.writeValueAsString(courtUpdate);

        final String superAdminToken = authClient.getSuperAdminToken();
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
            emptyList()
        );
        final String json = OBJECT_MAPPER.writeValueAsString(courtUpdate);

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
        final String token = authClient.getSuperAdminToken();
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + token)
            .body(json)
            .when()
            .put("/courts/info")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());

        final var getResponse = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + token)
            .when()
            .get(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(getResponse.statusCode()).isEqualTo(OK.value());
        final Court court = getResponse.as(Court.class);
        assertThat(court.getInfo()).isEqualTo(courtInfo.getInfo());
        assertThat(court.getInfoCy()).isEqualTo(courtInfo.getInfoCy());
    }

}
