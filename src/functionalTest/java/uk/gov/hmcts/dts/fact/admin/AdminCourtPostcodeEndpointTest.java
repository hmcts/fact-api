package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.List;
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
    private static final String TEST_POSTCODE = "B999BB";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void adminShouldRetrieveCourtPostcodes() {
        final var response = doGetRequest(BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<String> results = response.body().jsonPath().getList(".", String.class);
        assertThat(results).hasSizeGreaterThan(1);
    }

    @Test
    public void superAdminShouldRetrieveCourtPostcodes() {
        final var response = doGetRequest(BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<String> results = response.body().jsonPath().getList(".", String.class);
        assertThat(results).hasSizeGreaterThan(1);
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

    @Test
    public void adminShouldUpdateBeForbiddenFromUpdatingCourtPostcodes() throws JsonProcessingException {
        final List<String> currentPostcodes = getCurrentPostcodes();
        // Add a new postcode
        final String json = getPostcodeJson(addNewPostcode(currentPostcodes));
        final var response = doPutRequest(BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), json);
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void superAdminShouldUpdateCourtPostcodes() throws JsonProcessingException {
        final List<String> currentPostcodes = getCurrentPostcodes();

        // Add a new postcode
        String json = getPostcodeJson(addNewPostcode(currentPostcodes));
        var response = doPutRequest(BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken), json);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        List<String> updatedPostcodes = response.body().jsonPath().getList(".", String.class);
        assertThat(updatedPostcodes).contains(TEST_POSTCODE);

        // Remove the added postcode
        json = getPostcodeJson(removePostcode(updatedPostcodes));
        response = doPutRequest(BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + superAdminToken), json);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        updatedPostcodes = response.body().jsonPath().getList(".", String.class);
        assertThat(updatedPostcodes).doesNotContain(TEST_POSTCODE);
    }

    @Test
    public void shouldRequireATokenWhenUpdatingCourtPostcodes() throws JsonProcessingException {
        final var response = doPutRequest(BIRMINGHAM_COURT_POSTCODES_PATH, getPostcodeJson(getCurrentPostcodes()));
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForUpdatingCourtPostcodes() throws JsonProcessingException {
        final var response = doPutRequest(BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken), getPostcodeJson(getCurrentPostcodes()));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    private List<String> getCurrentPostcodes() {
        final var response = doGetRequest(BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        return response.body().jsonPath().getList(".", String.class);
    }

    private String getPostcodeJson(final List<String> postcodes) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(postcodes);
    }

    private List<String> addNewPostcode(final List<String> postcodes) {
        final List<String> updatedPostcodes = new ArrayList<>(postcodes);
        updatedPostcodes.add(TEST_POSTCODE);
        return updatedPostcodes;
    }

    private List<String> removePostcode(final List<String> postcodes) {
        final List<String> updatedPostcodes = new ArrayList<>(postcodes);
        updatedPostcodes.removeIf(p -> p.equals(TEST_POSTCODE));
        return updatedPostcodes;
    }
}
