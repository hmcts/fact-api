package uk.gov.hmcts.dts.fact.admin;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.model.admin.Region;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;


@SuppressWarnings("PMD.TooManyMethods")
class AdminRegionEndpointTest extends AdminFunctionalTestBase {
    private static final String ADMIN_REGIONS_ENDPOINT = "/admin/regions";

    @Test
    void shouldReturnAllRegions() {
        final Response response = doGetRequest(
            ADMIN_REGIONS_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<Region> adminRegions = response.body().jsonPath().getList(".", Region.class);
        assertThat(adminRegions).hasSizeGreaterThan(1);
        assertThat(adminRegions.stream()).anyMatch(r -> "North West".equals(r.getName()));
        assertThat(adminRegions.stream()).anyMatch(c -> "England".equals(c.getCountry()));
    }

    @Test
    void shouldRequireATokenWhenGettingAllRegions() {
        final Response response = doGetRequest(ADMIN_REGIONS_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingAllRegions() {
        final Response response = doGetRequest(
            ADMIN_REGIONS_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

}
