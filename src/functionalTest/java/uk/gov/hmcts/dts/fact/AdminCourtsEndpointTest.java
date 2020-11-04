package uk.gov.hmcts.dts.fact;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {OAuthClient.class})
public class AdminCourtsEndpointTest {
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE
        = "birmingham-civil-and-family-justice-centre";
    private static final String CONTENT_TYPE_VALUE = "application/json";
    private static final String COURT_DETAIL_BY_SLUG_ENDPOINT = "/courts/";

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @Autowired
    private OAuthClient authClient;

    private String token;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        token = authClient.getToken();
    }

    @Test
    public void shouldRetrieveAllCourts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, "Bearer " + token)
            .when()
            .get("/courts/all")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        String slug = response.jsonPath().get("[0].slug");
        assertThat(slug).isEqualTo("birkenhead-county-court-and-family-court");
    }

    @Test
    public void shouldRequireATokenForAllCourts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get("/courts/all")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    public void shouldRetrieveCourtGeneralBySlug() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, "Bearer " + token)
            .when()
            .get(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE + "/general")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final CourtGeneral court = response.as(CourtGeneral.class);
        assertThat(court.getName()).isEqualTo("Birmingham Civil and Family Justice Centre");
    }

    @Test
    public void shouldUpdateCourtGeneralBySlugAsAdmin() throws Exception {
        CourtGeneral courtGeneral = new CourtGeneral(
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre Admin Info",
            "Birmingham Civil and Family Justice Centre Admin Info",
            true,
            "Birmingham Civil and Family Justice Centre Admin Alert",
            "Birmingham Civil and Family Justice Centre Admin Alert"
        );

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(courtGeneral);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, "Bearer " + token)
            .body(json)
            .when()
            .put(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE + "/general")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final CourtGeneral court = response.as(CourtGeneral.class);
        assertThat(court.getAlert()).isEqualTo(courtGeneral.getAlert());
        assertThat(court.getAlertCy()).isEqualTo(courtGeneral.getAlertCy());
        assertThat(court.getInfo()).isNotEqualTo(courtGeneral.getInfo());
        assertThat(court.getInfoCy()).isNotEqualTo(courtGeneral.getInfoCy());
    }

    @Test
    public void shouldUpdateCourtGeneralBySlugAsSuperAdmin() throws Exception {
        CourtGeneral courtGeneral = new CourtGeneral(
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre Super Admin Info",
            "Birmingham Civil and Family Justice Centre Super Admin Info",
            true,
            "Birmingham Civil and Family Justice Centre Super Admin Alert",
            "Birmingham Civil and Family Justice Centre Super Admin Alert"
        );

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(courtGeneral);

        token = authClient.getSuperAdminToken();
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, "Bearer " + token)
            .body(json)
            .when()
            .put(COURT_DETAIL_BY_SLUG_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE + "/general")
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(200);
        final CourtGeneral court = response.as(CourtGeneral.class);
        assertThat(court.getAlert()).isEqualTo(courtGeneral.getAlert());
        assertThat(court.getAlertCy()).isEqualTo(courtGeneral.getAlertCy());
        assertThat(court.getInfo()).isEqualTo(courtGeneral.getInfo());
        assertThat(court.getInfoCy()).isEqualTo(courtGeneral.getInfoCy());
    }
}
