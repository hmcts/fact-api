package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@SuppressWarnings("PMD.TooManyMethods")
@ExtendWith(SpringExtension.class)
public class AdminCourtPostcodeEndpointTest extends AdminFunctionalTestBase {
    private static final String COURT_POSTCODES_PATH = "/postcodes";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG = "birmingham-civil-and-family-justice-centre";
    private static final String BIRMINGHAM_COURT_POSTCODES_PATH = ADMIN_COURTS_ENDPOINT
        + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG
        + COURT_POSTCODES_PATH;
    private static final String COURT_NOT_FIND_PATH = ADMIN_COURTS_ENDPOINT
        + "birmingham-civil-and-fay-justice-centre" + COURT_POSTCODES_PATH;
    private static final List<String> POSTCODES_VALID = Arrays.asList("B14 4BH", "B14 4JS");
    private static final List<String> POSTCODES_INVALID = Arrays.asList("ba62rt345435435", "da163rtgghg", "B1414545657");
    private static final List<String> POSTCODES_ALREADY_THERE = Arrays.asList("B139", "B144");
    private static final List<String> POSTCODES_DO_NOT_EXIST = Arrays.asList("B140", "B141", "B142");

    /************************************************************* GET request tests section. ***************************************************************/
    @Test
    public void adminShouldRetrieveCourtPostcodes() {
        final var response = doGetRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.body().jsonPath().getList(".", String.class)).hasSizeGreaterThan(1);
    }

    @Test
    public void adminShouldNotRetrievePostcodes() {
        final var response = doGetRequest(COURT_NOT_FIND_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void shouldRequireATokenWhenRetrievingCourtPostcodes() {
        final var response = doGetRequest(BIRMINGHAM_COURT_POSTCODES_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForRetrievingCourtPostcodes() {
        final var response = doGetRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* POST request tests section. ***************************************************************/
    @Test
    public void shouldCreateValidPostcodes() throws JsonProcessingException {

        final List<String> currentPostcodes = getCurrentPostcodes();
        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_VALID);
        final var response = doPostRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        final List<String> updatedCourtPostcodes = response.body().jsonPath().getList(
            ".",
            String.class
        );
        assertThat(updatedCourtPostcodes).containsExactlyElementsOf(POSTCODES_VALID);

        //clean up by removing added record
        final var cleanUpResponse = doDeleteRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        assertThat(cleanUpResponse.getBody().as(int.class)).isEqualTo(POSTCODES_VALID.size());

        final var responseAfterClean = doGetRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );

        final List<String> cleanCourtPostcodes = responseAfterClean.body().jsonPath().getList(
            ".",
            String.class
        );
        assertThat(cleanCourtPostcodes).containsExactlyElementsOf(currentPostcodes);
    }

    @Test
    public void shouldNotCreateInvalidPostcodes() throws JsonProcessingException {

        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_INVALID);

        final var response = doPostRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void adminShouldBeForbiddenForCreatingPostcodes() throws JsonProcessingException {

        final var response = doPostRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void adminShouldRequireATokenWhenCreatingCourtPostcodes() throws JsonProcessingException {
        final var response = doPostRequest(BIRMINGHAM_COURT_POSTCODES_PATH, getTestPostcodesJson());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void adminShouldNotBeAbleToCreatePostcodesWhenCourtNotFound() throws JsonProcessingException {

        final var response = doPostRequest(
            COURT_NOT_FIND_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    //409
    @Test
    public void adminShouldNotBeAbleToCreateDuplicatePostcodes() throws JsonProcessingException {

        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_ALREADY_THERE);

        final var response = doPostRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    private List<String> getCurrentPostcodes() {
        final var response = doGetRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        return response.body().jsonPath().getList(".", String.class);
    }

    private static String getTestPostcodesJson() throws JsonProcessingException {
        final List<String> postcodes = Arrays.asList("B140", "B141", "B142");
        return objectMapper().writeValueAsString(postcodes);
    }

    /************************************************************* Delete request tests section. ***************************************************************/
    @Test
    public void shouldNotDeleteInvalidPostcodes() throws JsonProcessingException {

        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_INVALID);

        final var response = doDeleteRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void adminShouldBeForbiddenForDeletingPostcodes() throws JsonProcessingException {

        final var response = doDeleteRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void adminShouldRequireATokenWhenDeletingCourtPostcodes() throws JsonProcessingException {
        final var response = doDeleteRequest(BIRMINGHAM_COURT_POSTCODES_PATH, getTestPostcodesJson());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void adminDeletePostcodesDoNotExist() throws JsonProcessingException {

        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_DO_NOT_EXIST);

        final var response = doPostRequest(
            COURT_NOT_FIND_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

}
