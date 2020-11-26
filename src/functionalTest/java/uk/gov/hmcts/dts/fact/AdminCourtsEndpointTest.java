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
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
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
    private static final String COURT_DETAIL_BY_SLUG_ENDPOINT = "/courts/";
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
    public void shouldRetrieveCourtGeneralBySlug() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final CourtGeneral court = response.as(CourtGeneral.class);
        assertThat(court.getName()).isEqualTo(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE);
    }

    @Test
    public void shouldBeForbiddenFromGettingCourtGeneralBySlug() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldUpdateCourtGeneralBySlugAsAdmin() throws Exception {
        final CourtGeneral courtGeneral = new CourtGeneral(
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            "Admin Info",
            "Welsh Admin Info",
            true,
            "Admin Alert",
            "Welsh Admin Alert"
        );

        final String json = OBJECT_MAPPER.writeValueAsString(courtGeneral);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(json)
            .when()
            .put(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final CourtGeneral court = response.as(CourtGeneral.class);
        assertThat(court.getAlert()).isEqualTo(courtGeneral.getAlert());
        assertThat(court.getAlertCy()).isEqualTo(courtGeneral.getAlertCy());
        assertThat(court.getInfo()).isNotEqualTo(courtGeneral.getInfo());
        assertThat(court.getInfoCy()).isNotEqualTo(courtGeneral.getInfoCy());
    }

    @Test
    public void shouldBeForbiddenToUpdateCourtGeneralBySlug() throws Exception {
        final CourtGeneral courtGeneral = new CourtGeneral(
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            "Admin Info",
            "Welsh Admin Info",
            true,
            "Admin Alert",
            "Welsh Admin Alert"
        );

        final String json = OBJECT_MAPPER.writeValueAsString(courtGeneral);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .body(json)
            .when()
            .put(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldUpdateCourtGeneralBySlugAsSuperAdmin() throws Exception {
        final CourtGeneral courtGeneral = new CourtGeneral(
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            "Super Admin Info",
            "Super Welsh Admin Info",
            true,
            "Super Admin Alert",
            "Super Welsh Admin Alert"
        );

        final String json = OBJECT_MAPPER.writeValueAsString(courtGeneral);

        final String superAdminToken = authClient.getSuperAdminToken();
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + superAdminToken)
            .body(json)
            .when()
            .put(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final CourtGeneral court = response.as(CourtGeneral.class);
        assertThat(court.getAlert()).isEqualTo(courtGeneral.getAlert());
        assertThat(court.getAlertCy()).isEqualTo(courtGeneral.getAlertCy());
        assertThat(court.getInfo()).isEqualTo(courtGeneral.getInfo());
        assertThat(court.getInfoCy()).isEqualTo(courtGeneral.getInfoCy());
    }

    @Test
    public void shouldNotUpdateCourtAsNoTokenProvided() throws Exception {
        final CourtGeneral courtGeneral = new CourtGeneral(
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE,
            "Admin Info",
            "Welsh Admin Info",
            true,
            "Admin Alert",
            "Welsh Admin Alert"
        );
        final String json = OBJECT_MAPPER.writeValueAsString(courtGeneral);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .body(json)
            .when()
            .put(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
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

        assertThat(response.statusCode()).isEqualTo(204);

        final var getResponse = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + token)
            .when()
            .get(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + COURT_GENERAL_ENDPOINT)
            .thenReturn();

        assertThat(getResponse.statusCode()).isEqualTo(200);
        final CourtGeneral court = getResponse.as(CourtGeneral.class);
        assertThat(court.getInfo()).isEqualTo(courtInfo.getInfo());
        assertThat(court.getInfoCy()).isEqualTo(courtInfo.getInfoCy());
    }

}
