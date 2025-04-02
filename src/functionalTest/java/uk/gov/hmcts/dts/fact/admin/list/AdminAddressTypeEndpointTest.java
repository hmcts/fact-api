package uk.gov.hmcts.dts.fact.admin.list;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.AddressType;
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
class AdminAddressTypeEndpointTest extends AdminFunctionalTestBase {

    private static final String ADDRESS_TYPES_ENDPOINT = "/admin/addressTypes";

    @Test
    void shouldGetAllAddressTypes() {
        final Response response = doGetRequest(ADDRESS_TYPES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<AddressType> addressType = response.body().jsonPath().getList(".", AddressType.class);
        assertThat(addressType).hasSizeGreaterThan(1);
    }

    @Test
    void shouldRequireATokenWhenGettingAllAddressTypes() {
        final Response response = doGetRequest(ADDRESS_TYPES_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingAllAddressTypes() {
        final Response response = doGetRequest(ADDRESS_TYPES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }
}
