package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
public class AdminCourtGeneralInfoEndpointTest extends AdminFunctionalTestBase {
    private static final String ADMIN_COURT_GENERAL_INFO_PATH = "/generalInfo";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG = "birmingham-civil-and-family-justice-centre";
    private static final CourtGeneralInfo EXPECTED_ADMIN_COURT_INFO = new CourtGeneralInfo(
        true,
        false,
        "Admin Info",
        "Welsh Admin Info",
        "Admin Alert",
        "Welsh Admin Alert"
    );
    private static final CourtGeneralInfo EXPECTED_SUPER_ADMIN_COURT_INFO = new CourtGeneralInfo(
        true,
        false,
        "Super Admin Info",
        "Super Welsh Admin Info",
        "Super Admin Alert",
        "Super Welsh Admin Alert"
    );

    private static String adminCourtInfoJson;

    @BeforeAll
    static void initialise() throws JsonProcessingException {
        adminCourtInfoJson = new ObjectMapper().writeValueAsString(EXPECTED_ADMIN_COURT_INFO);
    }

    @Test
    public void shouldRetrieveCourtGeneralInfo() {
        final Court expectedCourtDetails = getCourtDetails(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG);

        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + ADMIN_COURT_GENERAL_INFO_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());

        final CourtGeneralInfo generalInfo = response.as(CourtGeneralInfo.class);
        assertThat(generalInfo.getOpen()).isEqualTo(expectedCourtDetails.getOpen());
        assertThat(generalInfo.getAccessScheme()).isEqualTo(expectedCourtDetails.getAccessScheme());
        assertThat(generalInfo.getInfo()).isEqualTo(expectedCourtDetails.getInfo());
        assertThat(generalInfo.getAlert()).isEqualTo(expectedCourtDetails.getAlert());
    }

    @Test
    public void shouldRequireATokenWhenRetrievingCourtGeneralInfo() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + ADMIN_COURT_GENERAL_INFO_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForRetrievingCourtGeneralInfo() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + ADMIN_COURT_GENERAL_INFO_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldUpdateSelectedCourtGeneralInfoAsAdmin() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(adminCourtInfoJson)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + ADMIN_COURT_GENERAL_INFO_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());

        final CourtGeneralInfo result = response.as(CourtGeneralInfo.class);
        assertThat(result.getAlert()).isEqualTo(EXPECTED_ADMIN_COURT_INFO.getAlert());
        assertThat(result.getAlertCy()).isEqualTo(EXPECTED_ADMIN_COURT_INFO.getAlertCy());
        assertThat(result.getInfo()).isNotEqualTo(EXPECTED_ADMIN_COURT_INFO.getInfo());
        assertThat(result.getInfoCy()).isNotEqualTo(EXPECTED_ADMIN_COURT_INFO.getInfoCy());
    }

    @Test
    public void shouldUpdateAllCourtGeneralInfoAsSuperAdmin() throws JsonProcessingException {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + superAdminToken)
            .body(new ObjectMapper().writeValueAsString(EXPECTED_SUPER_ADMIN_COURT_INFO))
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + ADMIN_COURT_GENERAL_INFO_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());

        final CourtGeneralInfo result = response.as(CourtGeneralInfo.class);
        assertThat(result.getAlert()).isEqualTo(EXPECTED_SUPER_ADMIN_COURT_INFO.getAlert());
        assertThat(result.getAlertCy()).isEqualTo(EXPECTED_SUPER_ADMIN_COURT_INFO.getAlertCy());
        assertThat(result.getInfo()).isEqualTo(EXPECTED_SUPER_ADMIN_COURT_INFO.getInfo());
        assertThat(result.getInfoCy()).isEqualTo(EXPECTED_SUPER_ADMIN_COURT_INFO.getInfoCy());
    }

    @Test
    public void shouldRequireATokenWhenUpdatingCourtGeneralInfo() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .body(adminCourtInfoJson)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + ADMIN_COURT_GENERAL_INFO_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForUpdatingCourtGeneralInfo() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .body(adminCourtInfoJson)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + ADMIN_COURT_GENERAL_INFO_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    private static Court getCourtDetails(final String slug) {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURTS_ENDPOINT + slug)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        return response.as(Court.class);
    }
}
