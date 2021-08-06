package uk.gov.hmcts.dts.fact.admin.list;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.SidebarLocation;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
public class AdminSidebarLocationEndpointTest extends AdminFunctionalTestBase {

    private static final String SIDEBAR_LOCATION_ENDPOINT = "/admin/sidebarLocations";

    @Test
    public void shouldReturnAllSidebarLocations() {
        final Response response = doGetRequest(SIDEBAR_LOCATION_ENDPOINT, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<SidebarLocation> sidebarLocation = response.body().jsonPath().getList(".", SidebarLocation.class);
        assertThat(sidebarLocation).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingAllSidebarLocations() {
        final Response response = doGetRequest(SIDEBAR_LOCATION_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAllSidebarLocations() {
        final Response response = doGetRequest(SIDEBAR_LOCATION_ENDPOINT, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }
}
