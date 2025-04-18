package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith(SpringExtension.class)
class AdminCourtLocalAuthoritiesEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String LOCAL_AUTHORITIES_PATH = "localAuthorities";
    private static final String AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG = "aylesbury-county-court-and-family-court/";
    private static final String AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_AREAS_OF_LAW = "Money claims/";

    private static final String AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH = ADMIN_COURTS_ENDPOINT + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG
        + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_AREAS_OF_LAW + LOCAL_AUTHORITIES_PATH;
    //changed local authority to existing one on database.
    private static final String TEST = "Barnet Borough Council";
    private static final int TEST_ID = 397_243;

    @Test
    void returnLocalAuthoritiesForTheCourtAsPerTheAreasOfLaw() {
        final var response = doGetRequest(
            AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<LocalAuthority> localAuthorities = response.body().jsonPath().getList(".", LocalAuthority.class);
        assertThat(localAuthorities).hasSizeGreaterThan(1);
    }

    @Test
    void shouldRequireATokenWhenGettingLocalAuthoritiesForAreasOfLaw() {
        final var response = doGetRequest(AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingLocalAuthoritiesForAreasOfLaw() {
        final var response = doGetRequest(
            AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldUpdateCourtLocalAuthorities() throws JsonProcessingException {
        final List<LocalAuthority> currentCourtLocalAuthorities = getCurrentLocalAuthorities();
        final List<LocalAuthority> expectedCourtLocalAuthorities = updateLocalAuthorities(currentCourtLocalAuthorities);
        final String updatedJson = objectMapper().writeValueAsString(expectedCourtLocalAuthorities);
        final String originalJson = objectMapper().writeValueAsString(currentCourtLocalAuthorities);

        final var response = doPutRequest(
            AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<LocalAuthority> updatedCourtLocalAuthorities = response.body().jsonPath().getList(
            ".",
            LocalAuthority.class
        );
        assertThat(updatedCourtLocalAuthorities).containsExactlyElementsOf(expectedCourtLocalAuthorities);

        //clean up by removing added record
        final var cleanUpResponse = doPutRequest(
            AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());

        final List<LocalAuthority> cleanCourtLocalAuthorities = cleanUpResponse.body().jsonPath().getList(
            ".",
            LocalAuthority.class
        );
        assertThat(cleanCourtLocalAuthorities).containsExactlyElementsOf(currentCourtLocalAuthorities);
    }

    @Test
    void adminShouldBeForbiddenForUpdatingLocalAuthoritiesForAreasOfLaw() throws JsonProcessingException {

        final var response = doPutRequest(
            AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), getTestLocalAuthorityJson()

        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    private List<LocalAuthority> getCurrentLocalAuthorities() {
        final var response = doGetRequest(
            AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        return response.body().jsonPath().getList(".", LocalAuthority.class);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private List<LocalAuthority> updateLocalAuthorities(final List<LocalAuthority> localAuthorities) {
        List<LocalAuthority> updatedLocalAuthorities = new ArrayList<>(localAuthorities);
        LocalAuthority localAuthority = new LocalAuthority();
        localAuthority.setName(TEST);
        localAuthority.setId(TEST_ID);
        updatedLocalAuthorities.add(localAuthority);

        return updatedLocalAuthorities;
    }

    private static String getTestLocalAuthorityJson() throws JsonProcessingException {
        final List<LocalAuthority> localAuthorities = Arrays.asList(
            new LocalAuthority(1, "test1"),
            new LocalAuthority(2, "test2")

        );
        return objectMapper().writeValueAsString(localAuthorities);
    }
}
