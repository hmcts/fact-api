package uk.gov.hmcts.dts.fact.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
public class AdminCourtPostcodeEndpointTest extends AdminFunctionalTestBase {
    private static final String COURT_POSTCODES_PATH = "/postcodes";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG = "birmingham-civil-and-family-justice-centre";
    private static final String BIRMINGHAM_COURT_POSTCODES_PATH = ADMIN_COURTS_ENDPOINT
        + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG
        + COURT_POSTCODES_PATH;

    @Test
    public void adminShouldRetrieveCourtPostcodes() {
        final var response = doGetRequest(BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.body().jsonPath().getList(".", String.class)).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenRetrievingCourtPostcodes() {
        final var response = doGetRequest(BIRMINGHAM_COURT_POSTCODES_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForRetrievingCourtPostcodes() {
        final var response = doGetRequest(BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }
}
