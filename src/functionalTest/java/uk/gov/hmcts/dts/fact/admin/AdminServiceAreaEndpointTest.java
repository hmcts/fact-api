package uk.gov.hmcts.dts.fact.admin;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;

@SuppressWarnings("PMD.TooManyMethods")
class AdminServiceAreaEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_SERVICE_AREAS_ENDPOINT = "/admin/serviceAreas";

    @Test
    void shouldReturnAllServiceAreas() {
        final Response response = doGetRequest(
            ADMIN_SERVICE_AREAS_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<ServiceArea> adminServiceAreas = response.body().jsonPath().getList(".", ServiceArea.class);
        assertThat(adminServiceAreas).hasSizeGreaterThan(1);
    }

    @Test
    void shouldRequireATokenWhenGettingAllServiceAreas() {
        final Response response = doGetRequest(ADMIN_SERVICE_AREAS_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingAllServiceAreas() {
        final Response response = doGetRequest(
            ADMIN_SERVICE_AREAS_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

}
