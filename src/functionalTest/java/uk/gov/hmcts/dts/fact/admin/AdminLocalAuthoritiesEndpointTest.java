package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
public class AdminLocalAuthoritiesEndpointTest extends AdminFunctionalTestBase {

    private static final String LOCAL_AUTHORITIES_ENDPOINT = "/admin/localauthorities";
    private static final String GET_ALL_LOCAL_AUTHORITIES_ENDPOINT = LOCAL_AUTHORITIES_ENDPOINT + "/all";
    private static final String BIRMINGHAM_CITY_COUNCIL = "Birmingham City Council";

    @Test
    public void shouldGetAllLocalAuthorities() {
        final Response response = doGetRequest(GET_ALL_LOCAL_AUTHORITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<LocalAuthority> localAuthorities = response.body().jsonPath().getList(".", LocalAuthority.class);
        assertThat(localAuthorities).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingAllLocalAuthorities() {
        final Response response = doGetRequest(GET_ALL_LOCAL_AUTHORITIES_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingLocalAuthorities() {
        final Response response = doGetRequest(GET_ALL_LOCAL_AUTHORITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldUpdateExistingLocalAuthority() throws JsonProcessingException {
        // Get all local authorities
        final Response getResponse = doGetRequest(GET_ALL_LOCAL_AUTHORITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + superAdminToken));
        final List<LocalAuthority> localAuthorities = getResponse.body().jsonPath().getList(".", LocalAuthority.class);
        assertThat(localAuthorities).hasSizeGreaterThan(1);

        // Edit second local authority
        final LocalAuthority secondLocalAuthority = localAuthorities.get(1);
        final String path = LOCAL_AUTHORITIES_ENDPOINT + "/" + secondLocalAuthority.getId();
        final Response response = doPutRequest(path, Map.of(AUTHORIZATION, BEARER + superAdminToken), objectMapper().writeValueAsString(secondLocalAuthority));
        final LocalAuthority updatedLocalAuthority = response.as(LocalAuthority.class);
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(updatedLocalAuthority.getName()).isEqualTo(secondLocalAuthority.getName());
    }

    @Test
    public void shouldBeNotFoundForUpdateWhereLocalAuthorityDoesNotExist() throws JsonProcessingException {
        final String localAuthority = objectMapper().writeValueAsString(new LocalAuthority(9_999_999, BIRMINGHAM_CITY_COUNCIL));
        final Response response = doPutRequest(LOCAL_AUTHORITIES_ENDPOINT + "/9999999", Map.of(AUTHORIZATION, BEARER + superAdminToken), localAuthority);
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void shouldBeBadRequestForUpdateLocalAuthorityToInvalidName() throws JsonProcessingException {
        // Get all local authorities
        final Response getResponse = doGetRequest(GET_ALL_LOCAL_AUTHORITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + superAdminToken));
        final List<LocalAuthority> localAuthorities = getResponse.body().jsonPath().getList(".", LocalAuthority.class);
        assertThat(localAuthorities).hasSizeGreaterThan(1);

        // Attempt to edit existing valid local authority to have an invalid name
        final LocalAuthority secondLocalAuthority = localAuthorities.get(1);
        final String invalidRequestBody = objectMapper().writeValueAsString(new LocalAuthority(secondLocalAuthority.getId(), "Briminghm Council"));
        final String path = String.format("%s/%s", LOCAL_AUTHORITIES_ENDPOINT, secondLocalAuthority.getId());
        final Response response = doPutRequest(path, Map.of(AUTHORIZATION, BEARER + superAdminToken), invalidRequestBody);
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void shouldRequireATokenWhenUpdatingLocalAuthority() throws JsonProcessingException {
        final String localAuthority = objectMapper().writeValueAsString(new LocalAuthority(54_321, BIRMINGHAM_CITY_COUNCIL));
        final Response response = doPutRequest(LOCAL_AUTHORITIES_ENDPOINT + "/54321", localAuthority);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForUpdatingLocalAuthority() throws JsonProcessingException {
        final String localAuthority = objectMapper().writeValueAsString(new LocalAuthority(54_321, BIRMINGHAM_CITY_COUNCIL));
        final Response response = doPutRequest(LOCAL_AUTHORITIES_ENDPOINT + "/12345", Map.of(AUTHORIZATION, BEARER + forbiddenToken), localAuthority);
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldBeForbiddenForAdminUpdatingLocalAuthority() throws JsonProcessingException {
        final String localAuthority = objectMapper().writeValueAsString(new LocalAuthority(54_321, BIRMINGHAM_CITY_COUNCIL));
        final Response response = doPutRequest(LOCAL_AUTHORITIES_ENDPOINT + "/54321",  Map.of(AUTHORIZATION, BEARER + authenticatedToken), localAuthority);
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }
}
