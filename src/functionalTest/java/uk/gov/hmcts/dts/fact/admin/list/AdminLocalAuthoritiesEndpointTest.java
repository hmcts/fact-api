package uk.gov.hmcts.dts.fact.admin.list;

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
    private static final String NON_EXISTENT_LOCAL_AUTHORITY = "Briminghm Council";

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
    public void shouldUpdateExistingLocalAuthority() {
        // Get all local authorities
        final Response getResponse = doGetRequest(GET_ALL_LOCAL_AUTHORITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + superAdminToken));
        final List<LocalAuthority> localAuthorities = getResponse.body().jsonPath().getList(".", LocalAuthority.class);
        assertThat(localAuthorities).hasSizeGreaterThan(1);

        // Edit second local authority
        final LocalAuthority secondLocalAuthority = localAuthorities.get(1);
        final String path = LOCAL_AUTHORITIES_ENDPOINT + "/" + secondLocalAuthority.getId();
        final Response response = doPutRequest(path, Map.of(AUTHORIZATION, BEARER + superAdminToken, "Content-Type", "text/plain"), secondLocalAuthority.getName());
        final LocalAuthority updatedLocalAuthority = response.as(LocalAuthority.class);
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(updatedLocalAuthority.getName()).isEqualTo(secondLocalAuthority.getName());
    }

    @Test
    public void shouldBeConflictForUpdateWhenLocalAuthorityAlreadyExists() {
        // Get all local authorities
        final Response getResponse = doGetRequest(GET_ALL_LOCAL_AUTHORITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + superAdminToken));
        final List<LocalAuthority> localAuthorities = getResponse.body().jsonPath().getList(".", LocalAuthority.class);
        assertThat(localAuthorities).hasSizeGreaterThan(1);

        // Attempt to edit second local authority to be same as first local authority
        final LocalAuthority firstLocalAuthority = localAuthorities.get(0);
        final LocalAuthority secondLocalAuthority = localAuthorities.get(1);

        final String path = LOCAL_AUTHORITIES_ENDPOINT + "/" + secondLocalAuthority.getId();
        final Response response = doPutRequest(path, Map.of(AUTHORIZATION, BEARER + superAdminToken), firstLocalAuthority.getName());

        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    @Test
    public void shouldBeNotFoundForUpdateWhereLocalAuthorityDoesNotExist() {
        final Response response = doPutRequest(LOCAL_AUTHORITIES_ENDPOINT + "/9999999",
                                               Map.of(AUTHORIZATION, BEARER + superAdminToken),
                                               BIRMINGHAM_CITY_COUNCIL);
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void shouldBeBadRequestForUpdateLocalAuthorityToInvalidName() {
        // Get all local authorities
        final Response getResponse = doGetRequest(GET_ALL_LOCAL_AUTHORITIES_ENDPOINT, Map.of(AUTHORIZATION, BEARER + superAdminToken));
        final List<LocalAuthority> localAuthorities = getResponse.body().jsonPath().getList(".", LocalAuthority.class);
        assertThat(localAuthorities).hasSizeGreaterThan(1);

        // Attempt to edit existing valid local authority to have an invalid name
        final String path = String.format("%s/%s", LOCAL_AUTHORITIES_ENDPOINT, localAuthorities.get(1).getId());
        final Response response = doPutRequest(path, Map.of(AUTHORIZATION, BEARER + superAdminToken), NON_EXISTENT_LOCAL_AUTHORITY);
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void shouldRequireATokenWhenUpdatingLocalAuthority() {
        final Response response = doPutRequest(LOCAL_AUTHORITIES_ENDPOINT + "/54321", BIRMINGHAM_CITY_COUNCIL);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForUpdatingLocalAuthority() {
        final Response response = doPutRequest(LOCAL_AUTHORITIES_ENDPOINT + "/12345",
                                               Map.of(AUTHORIZATION, BEARER + forbiddenToken),
                                               BIRMINGHAM_CITY_COUNCIL);
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldBeForbiddenForAdminUpdatingLocalAuthority() {
        final Response response = doPutRequest(LOCAL_AUTHORITIES_ENDPOINT + "/54321",
                                               Map.of(AUTHORIZATION, BEARER + authenticatedToken),
                                               BIRMINGHAM_CITY_COUNCIL);
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }
}
