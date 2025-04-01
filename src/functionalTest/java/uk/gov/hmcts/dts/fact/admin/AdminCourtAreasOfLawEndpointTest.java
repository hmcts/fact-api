package uk.gov.hmcts.dts.fact.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;

@ExtendWith(SpringExtension.class)
class AdminCourtAreasOfLawEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String AREAS_OF_LAW_PATH = "courtAreasOfLaw";
    private static final String AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG = "aylesbury-county-court-and-family-court/";

    private static final String AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH = ADMIN_COURTS_ENDPOINT
        + AYLESBURY_COUNTY_COURT_AND_FAMILY_COURT_SLUG + AREAS_OF_LAW_PATH;


    @Test
    void returnAreasOfLawForTheCourt() {
        final var response = doGetRequest(
            AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<AreaOfLaw> areasOfLaw = response.body().jsonPath().getList(".", AreaOfLaw.class);
        assertThat(areasOfLaw).hasSizeGreaterThan(1);
    }

    @Test
    void shouldRequireATokenWhenGettingAreasOfLawForTheCourt() {
        final var response = doGetRequest(AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingAreasOfLawForTheCourt() {
        final var response = doGetRequest(
            AYLESBURY_COURT_LOCAL_AUTHORITIES_AREAS_OF_LAW_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

}
