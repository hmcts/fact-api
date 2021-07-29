package uk.gov.hmcts.dts.fact.admin.list;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;

public class AdminAreasOfLawEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_AREAS_OF_LAW_ENDPOINT = "/admin/courtAreasOfLaw";

    @Test
    public void shouldGetAllCourtAreasOfLaw() {
        final Response response = doGetRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<AreaOfLaw> courtAreasOfLaw = response.body().jsonPath().getList(".", AreaOfLaw.class);
        assertThat(courtAreasOfLaw).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingAllCourtAreasOfLaw() {
        final Response response = doGetRequest(ADMIN_AREAS_OF_LAW_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAllCourtAreasOfLaw() {
        final Response response = doGetRequest(
            ADMIN_AREAS_OF_LAW_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }
}
