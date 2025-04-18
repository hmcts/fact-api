package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.ADMIN_COURTS_ENDPOINT;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@SuppressWarnings("PMD.TooManyMethods")
@ExtendWith(SpringExtension.class)
class AdminCourtPostcodeEndpointTest extends AdminFunctionalTestBase {
    private static final String COURT_POSTCODES_PATH = "/postcodes";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG = "birmingham-civil-and-family-justice-centre";
    private static final String WOLVERHAMPTON_COMBINED_COURT_CENTRE_SLUG = "wolverhampton-combined-court-centre";
    private static final String BIRMINGHAM_COURT_POSTCODES_PATH = ADMIN_COURTS_ENDPOINT
        + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG
        + COURT_POSTCODES_PATH;
    private static final String WOLVERHAMPTON_COURT_POSTCODES_PATH = ADMIN_COURTS_ENDPOINT
        + WOLVERHAMPTON_COMBINED_COURT_CENTRE_SLUG
        + COURT_POSTCODES_PATH;
    private static final String BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH = ADMIN_COURTS_ENDPOINT
        + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG
        + "/" + WOLVERHAMPTON_COMBINED_COURT_CENTRE_SLUG
        + COURT_POSTCODES_PATH;
    private static final String WOLVERHAMPTON_TO_BIRMINGHAM_COURT_POSTCODES_PATH = ADMIN_COURTS_ENDPOINT
        + WOLVERHAMPTON_COMBINED_COURT_CENTRE_SLUG
        + "/" + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG
        + COURT_POSTCODES_PATH;
    private static final String COURT_NOT_FIND_PATH = ADMIN_COURTS_ENDPOINT
        + "birmingham-civil-and-fay-justice-centre" + COURT_POSTCODES_PATH;
    private static final String DELETE_LOCK_BY_EMAIL_PATH = ADMIN_COURTS_ENDPOINT + "hmcts.fact@gmail.com/lock";
    private static final String NOT_FOUND_POSTCODE = "B119";
    private static final String CONFLICT_POSTCODE = "B75";

    private static final List<String> POSTCODES_VALID = Arrays.asList(
        "B26 1EE",
        "B26 1EJ"
    );
    private static final List<String> POSTCODES_INVALID = Arrays.asList(
        "ba62rt345435435",
        "da163rtgghg",
        "B144JS"
    );
    private static final List<String> POSTCODES_INVALID_RESPONSE = Arrays.asList(
        "ba62rt345435435",
        "da163rtgghg"
    );
    private static final List<String> POSTCODES_ALREADY_THERE = Arrays.asList(
        "B139",
        "B144"
    );
    private static final List<String> POSTCODES_DO_NOT_EXIST = Arrays.asList(
        "SE91AA",
        "SE91AB",
        "SE91AD"
    );
    private static final List<String> POSTCODES_DUPLICATE = Arrays.asList(
        "B21 0PA",
        "B21 0PD",
        "B21 0PP"
    );
    private static final List<String> POSTCODES_TO_MOVE = Arrays.asList(
        "B742SR",
        "B75"
    );

    private static final List<String> POSTCODES_TO_MOVE_NOT_FOUND = Arrays.asList(
        "B75",
        NOT_FOUND_POSTCODE
    );

    /************************************************************* GET request tests section. ***************************************************************/
    @Test
    void shouldRetrieveCourtPostcodes() {
        final var response = doGetRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.body().jsonPath().getList(".", String.class)).hasSizeGreaterThan(1);
    }

    @Test
    void shouldNotRetrievePostcodesWhenCourtSlugNotFound() {
        final var response = doGetRequest(COURT_NOT_FIND_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    void shouldRequireATokenWhenRetrievingCourtPostcodes() {
        final var response = doGetRequest(BIRMINGHAM_COURT_POSTCODES_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForRetrievingCourtPostcodes() {
        final var response = doGetRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* POST request tests section. ***************************************************************/
    @Test
    void shouldCreateValidPostcodes() throws JsonProcessingException {

        //calling user delete lock endpoint to remove lock for the court
        final var delResponse = doDeleteRequest(DELETE_LOCK_BY_EMAIL_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
                                                "");
        assertThat(delResponse.statusCode()).isEqualTo(OK.value());

        final List<String> currentPostcodes = getCurrentPostcodes(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG);
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
        final List<String> trimmedPostcodes = POSTCODES_VALID.stream().map(x -> x.replaceAll(" ", "")).collect(
            Collectors.toList());

        assertThat(updatedCourtPostcodes).containsExactlyElementsOf(trimmedPostcodes);

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
    void shouldNotCreateDuplicatePostcodes() throws JsonProcessingException {

        final var delResponse = doDeleteRequest(DELETE_LOCK_BY_EMAIL_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken),
                                                "");
        assertThat(delResponse.statusCode()).isEqualTo(OK.value());

        // Clean up both destination court postcodes if lingering from previous run failure
        cleanUpTestData(BIRMINGHAM_COURT_POSTCODES_PATH, objectMapper().writeValueAsString(POSTCODES_DUPLICATE));

        // Add initial postcode for test
        final List<String> currentPostcodes = getCurrentPostcodes(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG);
        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_DUPLICATE);
        Response response = doPostRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        final List<String> updatedCourtPostcodes = response.body().jsonPath().getList(
            ".",
            String.class
        );

        //Checking only unique values updated in database
        final Set<String> trimmedPostcodes = POSTCODES_DUPLICATE.stream().map(x -> x.replaceAll(" ", "")).collect(
            Collectors.toSet());
        assertThat(updatedCourtPostcodes).containsExactlyInAnyOrderElementsOf(trimmedPostcodes);

        //clean up by removing added record
        final var cleanUpResponse = doDeleteRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        assertThat(cleanUpResponse.getBody().as(int.class)).isEqualTo(trimmedPostcodes.size());

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
    void shouldNotCreateInvalidPostcodes() throws JsonProcessingException {

        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_INVALID);

        final var response = doPostRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        final String invalidPostcodes = response.body().jsonPath().getString("message");
        assertThat(invalidPostcodes).isEqualTo(String.join(",",POSTCODES_INVALID_RESPONSE));
    }

    @Test
    void adminShouldBeForbiddenForCreatingPostcodes() throws JsonProcessingException {

        final var response = doPostRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenCreatingCourtPostcodes() throws JsonProcessingException {
        final var response = doPostRequest(BIRMINGHAM_COURT_POSTCODES_PATH, getTestPostcodesJson());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotBeAbleToCreatePostcodesWhenCourtNotFound() throws JsonProcessingException {

        final var response = doPostRequest(
            COURT_NOT_FIND_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }


    @Test
    void shouldNotBeAbleToCreatePostcodesAlreadyExist() throws JsonProcessingException {

        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_ALREADY_THERE);

        final var response = doPostRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    /************************************************************* Delete request tests section. ***************************************************************/
    @Test
    void shouldNotDeleteInvalidPostcodes() throws JsonProcessingException {

        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_INVALID);

        final var response = doDeleteRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        final String invalidPostcodes = response.body().jsonPath().getString("message");
        assertThat(invalidPostcodes).isEqualTo(String.join(",",POSTCODES_INVALID_RESPONSE));
    }

    @Test
    void adminShouldBeForbiddenForDeletingPostcodes() throws JsonProcessingException {

        final var response = doDeleteRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenDeletingCourtPostcodes() throws JsonProcessingException {
        final var response = doDeleteRequest(BIRMINGHAM_COURT_POSTCODES_PATH, getTestPostcodesJson());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotDeletePostcodesDoNotExist() throws JsonProcessingException {

        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_DO_NOT_EXIST);

        final var response = doDeleteRequest(
            COURT_NOT_FIND_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* PUT request tests section. ***************************************************************/

    @Test
    void shouldMovePostcodesToADifferentCourt() throws JsonProcessingException {

        final String postcodesToMoveJson = objectMapper().writeValueAsString(POSTCODES_TO_MOVE);
        Response response = doGetRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.body().jsonPath().getList(".", String.class)).containsAll(POSTCODES_TO_MOVE);

        response = doGetRequest(
            WOLVERHAMPTON_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.body().jsonPath().getList(".", String.class))
            .doesNotContainAnyElementsOf(POSTCODES_TO_MOVE);

        response = doPutRequest(
            BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            postcodesToMoveJson
        );
        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.statusCode()).isEqualTo(OK.value());
        List<String> movedPostcodes = response.body().jsonPath().getList(".", String.class);
        softly.assertThat(movedPostcodes).containsExactlyElementsOf(POSTCODES_TO_MOVE);
        softly.assertThat(getCurrentPostcodes(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG))
            .doesNotContainAnyElementsOf(POSTCODES_TO_MOVE);
        softly.assertThat(getCurrentPostcodes(WOLVERHAMPTON_COMBINED_COURT_CENTRE_SLUG))
            .containsAnyElementsOf(POSTCODES_TO_MOVE);

        // Clean up by moving the postcodes back to its original court
        response = doPutRequest(
            WOLVERHAMPTON_TO_BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            postcodesToMoveJson
        );

        movedPostcodes = response.body().jsonPath().getList(".", String.class);
        softly.assertThat(response.statusCode()).isEqualTo(OK.value());
        softly.assertThat(movedPostcodes).containsExactlyElementsOf(POSTCODES_TO_MOVE);
        softly.assertThat(getCurrentPostcodes(WOLVERHAMPTON_COMBINED_COURT_CENTRE_SLUG))
            .doesNotContainAnyElementsOf(POSTCODES_TO_MOVE);
        softly.assertThat(getCurrentPostcodes(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG))
            .containsAnyElementsOf(POSTCODES_TO_MOVE);
        softly.assertAll();
    }

    @Test
    void adminShouldBeForbiddenForMovingPostcodes() throws JsonProcessingException {
        final var response = doPutRequest(
            BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenMovingPostcodes() throws JsonProcessingException {
        final var response = doPutRequest(
            BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH,
            getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotMovePostcodesIfNotInSourceCourts() throws JsonProcessingException {

        // Clean up both destination court postcodes if lingering from previous run failure
        cleanUpTestData(BIRMINGHAM_COURT_POSTCODES_PATH, objectMapper().writeValueAsString(POSTCODES_TO_MOVE_NOT_FOUND));

        final String postcodesToMoveJson = objectMapper().writeValueAsString(POSTCODES_TO_MOVE_NOT_FOUND);
        var response = doPutRequest(
            BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            postcodesToMoveJson
        );
        final List<String> notFoundPostcodes = response.body().jsonPath().getList(".", String.class);
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
        assertThat(notFoundPostcodes).hasSize(1)
            .first()
            .isEqualTo(NOT_FOUND_POSTCODE);
    }

    @Test
    void shouldNotMovePostcodesIfAlreadyExistsInDestinationCourt() throws JsonProcessingException {

        // Clean up both destination court postcodes if lingering from previous run failure
        cleanUpTestData(WOLVERHAMPTON_COURT_POSTCODES_PATH, objectMapper().writeValueAsString(singletonList(CONFLICT_POSTCODE)));

        // Add a conflicting postcode to the Wolverhampton court before moving from Birmingham court
        final String postcodesToAddJson = objectMapper().writeValueAsString(singletonList(CONFLICT_POSTCODE));
        var response = doPostRequest(
            WOLVERHAMPTON_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            postcodesToAddJson
        );
        assertThat(response.statusCode()).isEqualTo(CREATED.value());

        final String postcodesToMoveJson = objectMapper().writeValueAsString(POSTCODES_TO_MOVE);
        response = doPutRequest(
            BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            postcodesToMoveJson
        );

        final List<String> duplicatedPostcodes = response.body().jsonPath().getList(".", String.class);
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
        assertThat(duplicatedPostcodes).containsExactly(CONFLICT_POSTCODE);

        // Clean up by deleting postcode added to the Wolverhampton court
        response = doDeleteRequest(
            WOLVERHAMPTON_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            postcodesToAddJson
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    @Test
    void shouldNotMoveInvalidPostcodes() throws JsonProcessingException {
        final String invalidPostcodesToMoveJson = objectMapper().writeValueAsString(POSTCODES_INVALID);
        final var response = doPutRequest(
            BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            invalidPostcodesToMoveJson
        );
        final String invalidPostcodes = response.body().jsonPath().getString("message");
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(invalidPostcodes).isEqualTo(String.join(",",POSTCODES_INVALID_RESPONSE));
    }

    /************************************************************* Shared utility methods. ***************************************************************/

    private List<String> getCurrentPostcodes(final String courtSlug) {
        final var response = doGetRequest(
            ADMIN_COURTS_ENDPOINT + courtSlug + COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        return response.body().jsonPath().getList(".", String.class);
    }

    private static String getTestPostcodesJson() throws JsonProcessingException {
        final List<String> postcodes = Arrays.asList("B140", "B141", "B142");
        return objectMapper().writeValueAsString(postcodes);
    }

    private void cleanUpTestData(String path, String jsonToSend) {
        // Clean up both destination court postcodes if lingering
        Response response = doDeleteRequest(
            path,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            jsonToSend
        );
        assertThat(response.statusCode()).isIn(OK.value(), NOT_FOUND.value());
    }
}
