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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.ADMIN_COURTS_ENDPOINT;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.COURTS_ENDPOINT;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith(SpringExtension.class)
class AdminCourtGeneralInfoEndpointTest extends AdminFunctionalTestBase {
    private static final String ADMIN_COURT_GENERAL_INFO_PATH = "/generalInfo";
    // Do not use these court slugs on other tests, as the slug gets changed on the database. If you do,
    // make sure to add a cleanup below first
    private static final String DARTFORD_COURT_SLUG = "dartford-county-court-and-family-court";
    private static final String TEST_SLUG = "super-admin-name";
    private static final String DARTFORD_GENERAL_INFO_PATH = ADMIN_COURTS_ENDPOINT + DARTFORD_COURT_SLUG + ADMIN_COURT_GENERAL_INFO_PATH;
    private static final String TEST_PATH = ADMIN_COURTS_ENDPOINT + TEST_SLUG + ADMIN_COURT_GENERAL_INFO_PATH;
    private static final String DELETE_LOCK_BY_EMAIL_PATH = ADMIN_COURTS_ENDPOINT + "hmcts.fact@gmail.com/lock";
    private static final CourtGeneralInfo EXPECTED_ADMIN_COURT_INFO = new CourtGeneralInfo(
        "Admin name",
        true,
        true,
        false,
        "Admin Info",
        "Welsh Admin Info",
        "Admin Alert",
        "Welsh Admin Alert",
        "Intro paragraph",
        "Intro paragraph cy",
        false,
        false
    );
    private static final CourtGeneralInfo EXPECTED_SUPER_ADMIN_COURT_INFO = new CourtGeneralInfo(
        "Super Admin name",
        true,
        true,
        false,
        "Super Admin Info",
        "Super Welsh Admin Info",
        "Super Admin Alert",
        "Super Welsh Admin Alert",
        "Intro paragraph",
        "Intro paragraph cy",
        false,
        false
    );

    private static String adminCourtInfoJson;

    @BeforeAll
    static void initialise() throws JsonProcessingException {
        adminCourtInfoJson = new ObjectMapper().writeValueAsString(EXPECTED_ADMIN_COURT_INFO);
    }


    @Test
    void shouldRetrieveCourtGeneralInfo() {
        var response = doGetRequest(COURTS_ENDPOINT + DARTFORD_COURT_SLUG);
        final Court expectedCourtDetails = response.as(Court.class);

        response = doGetRequest(DARTFORD_GENERAL_INFO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final CourtGeneralInfo generalInfo = response.as(CourtGeneralInfo.class);
        assertThat(generalInfo.getName()).isEqualTo(expectedCourtDetails.getName());
        assertThat(generalInfo.getOpen()).isEqualTo(expectedCourtDetails.getOpen());
        assertThat(generalInfo.getInPerson()).isEqualTo(expectedCourtDetails.getInPerson());
        assertThat(generalInfo.getAccessScheme()).isEqualTo(expectedCourtDetails.getAccessScheme());
        assertThat(generalInfo.getInfo()).isEqualTo(expectedCourtDetails.getInfo());
        assertThat(generalInfo.getAlert()).isEqualTo(expectedCourtDetails.getAlert());
        assertThat(generalInfo.isCommonPlatform()).isEqualTo(expectedCourtDetails.getCommonFlag());
    }


    @Test
    void shouldRequireATokenWhenRetrievingCourtGeneralInfo() {
        final var response = doGetRequest(DARTFORD_GENERAL_INFO_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForRetrievingCourtGeneralInfo() {
        final var response = doGetRequest(DARTFORD_GENERAL_INFO_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldUpdateSelectedCourtGeneralInfoAsAdmin() {
        final var response = doPutRequest(DARTFORD_GENERAL_INFO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), adminCourtInfoJson);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final CourtGeneralInfo result = response.as(CourtGeneralInfo.class);
        assertThat(result.getAlert()).isEqualTo(EXPECTED_ADMIN_COURT_INFO.getAlert());
        assertThat(result.getAlertCy()).isEqualTo(EXPECTED_ADMIN_COURT_INFO.getAlertCy());
        assertThat(result.getInfo()).isNotEqualTo(EXPECTED_ADMIN_COURT_INFO.getInfo());
        assertThat(result.getInfoCy()).isNotEqualTo(EXPECTED_ADMIN_COURT_INFO.getInfoCy());
        assertThat(result.getName()).isNotEqualTo(EXPECTED_ADMIN_COURT_INFO.getName());


    }

    @Test
    void shouldUpdateCourtGeneralInfoAsSuperAdmin() throws JsonProcessingException {
        final var delResponse = doDeleteRequest(DELETE_LOCK_BY_EMAIL_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
                                                "");
        assertThat(delResponse.statusCode()).isEqualTo(OK.value());

        final var orgResponse = doGetRequest(DARTFORD_GENERAL_INFO_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(orgResponse.statusCode()).isEqualTo(OK.value());
        final CourtGeneralInfo generalInfo = orgResponse.as(CourtGeneralInfo.class);
        final String originalJson = objectMapper().writeValueAsString(generalInfo);

        final var response = doPutRequest(DARTFORD_GENERAL_INFO_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken),
                                          new ObjectMapper().writeValueAsString(EXPECTED_SUPER_ADMIN_COURT_INFO));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final CourtGeneralInfo result = response.as(CourtGeneralInfo.class);
        assertThat(result.getAlert()).isEqualTo(EXPECTED_SUPER_ADMIN_COURT_INFO.getAlert());
        assertThat(result.getAlertCy()).isEqualTo(EXPECTED_SUPER_ADMIN_COURT_INFO.getAlertCy());
        assertThat(result.getInfo()).isEqualTo(EXPECTED_SUPER_ADMIN_COURT_INFO.getInfo());
        assertThat(result.getInfoCy()).isEqualTo(EXPECTED_SUPER_ADMIN_COURT_INFO.getInfoCy());
        assertThat(result.getName()).isEqualTo(EXPECTED_SUPER_ADMIN_COURT_INFO.getName());

        // clean up step
        final var cleanUpResponse = doPutRequest(TEST_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken), originalJson);
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        final CourtGeneralInfo orgGeneralInfo = cleanUpResponse.as(CourtGeneralInfo.class);
        assertThat(orgGeneralInfo.getName()).isEqualTo(generalInfo.getName());
    }

    @Test
    void shouldRequireATokenWhenUpdatingCourtGeneralInfo() {
        final var response = doPutRequest(DARTFORD_GENERAL_INFO_PATH, adminCourtInfoJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForUpdatingCourtGeneralInfo() {
        final var response = doPutRequest(DARTFORD_GENERAL_INFO_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken), adminCourtInfoJson);
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }
}
