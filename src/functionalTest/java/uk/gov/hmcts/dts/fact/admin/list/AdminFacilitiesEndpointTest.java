package uk.gov.hmcts.dts.fact.admin.list;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.FacilityType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;


@ExtendWith(SpringExtension.class)
public class AdminFacilitiesEndpointTest extends AdminFunctionalTestBase {

    private static final String GET_FACILITIES_ENDPOINT = "/admin/facilities";

    @Test
    public void shouldReturnAllFacilities() {
        final Response response = doGetRequest(GET_FACILITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<FacilityType> facilities = response.body().jsonPath().getList(".", FacilityType.class);
        assertThat(facilities).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingFacilities() {
        final Response response = doGetRequest(GET_FACILITIES_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingFacilities() {
        final Response response = doGetRequest(GET_FACILITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

}
