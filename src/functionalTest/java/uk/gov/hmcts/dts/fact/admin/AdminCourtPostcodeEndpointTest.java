package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@SuppressWarnings("PMD.TooManyMethods")
@ExtendWith(SpringExtension.class)
public class AdminCourtPostcodeEndpointTest extends AdminFunctionalTestBase {
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
    public void shouldRetrieveCourtPostcodes() {
        final var response = doGetRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.body().jsonPath().getList(".", String.class)).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldNotRetrievePostcodesWhenCourtSlugNotFound() {
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
    public void shouldNotCreateDuplicatePostcodes() throws JsonProcessingException {

        final List<String> currentPostcodes = getCurrentPostcodes(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG);
        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_DUPLICATE);
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
    public void shouldNotCreateInvalidPostcodes() throws JsonProcessingException {

        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_INVALID);

        final var response = doPostRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        final List<String> invalidPostcodes = response.body().jsonPath().getList(".", String.class);
        assertThat(invalidPostcodes).containsExactlyInAnyOrderElementsOf(POSTCODES_INVALID_RESPONSE);
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
    public void shouldRequireATokenWhenCreatingCourtPostcodes() throws JsonProcessingException {
        final var response = doPostRequest(BIRMINGHAM_COURT_POSTCODES_PATH, getTestPostcodesJson());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotBeAbleToCreatePostcodesWhenCourtNotFound() throws JsonProcessingException {

        final var response = doPostRequest(
            COURT_NOT_FIND_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }


    @Test
    public void shouldNotBeAbleToCreatePostcodesAlreadyExist() throws JsonProcessingException {

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
    public void shouldNotDeleteInvalidPostcodes() throws JsonProcessingException {

        final String updatedJson = objectMapper().writeValueAsString(POSTCODES_INVALID);

        final var response = doDeleteRequest(
            BIRMINGHAM_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        final List<String> invalidPostcodes = response.body().jsonPath().getList(".", String.class);
        assertThat(invalidPostcodes).containsExactlyInAnyOrderElementsOf(POSTCODES_INVALID_RESPONSE);
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
    public void shouldRequireATokenWhenDeletingCourtPostcodes() throws JsonProcessingException {
        final var response = doDeleteRequest(BIRMINGHAM_COURT_POSTCODES_PATH, getTestPostcodesJson());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotDeletePostcodesDoNotExist() throws JsonProcessingException {

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
    public void shouldMovePostcodesToADifferentCourt() throws JsonProcessingException {
        final String postcodesToMoveJson = objectMapper().writeValueAsString(POSTCODES_TO_MOVE);
        var response = doPutRequest(
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

        softly.assertThat(response.statusCode()).isEqualTo(OK.value());
        movedPostcodes = response.body().jsonPath().getList(".", String.class);

        softly.assertThat(movedPostcodes).containsExactlyElementsOf(POSTCODES_TO_MOVE);
        softly.assertThat(getCurrentPostcodes(WOLVERHAMPTON_COMBINED_COURT_CENTRE_SLUG))
            .doesNotContainAnyElementsOf(POSTCODES_TO_MOVE);
        softly.assertThat(getCurrentPostcodes(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG))
            .containsAnyElementsOf(POSTCODES_TO_MOVE);
        softly.assertAll();
    }

    @Test
    public void adminShouldBeForbiddenForMovingPostcodes() throws JsonProcessingException {
        final var response = doPutRequest(
            BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenMovingPostcodes() throws JsonProcessingException {
        final var response = doPutRequest(
            BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH,
            getTestPostcodesJson()
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotMovePostcodesIfNotInSourceCourts() throws JsonProcessingException {
        final String postcodesToMoveJson = objectMapper().writeValueAsString(POSTCODES_TO_MOVE_NOT_FOUND);
        var response = doPutRequest(
            BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            postcodesToMoveJson
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());

        final List<String> notFoundPostcodes = response.body().jsonPath().getList(".", String.class);
        assertThat(notFoundPostcodes).hasSize(1)
            .first()
            .isEqualTo(NOT_FOUND_POSTCODE);
    }

    @Test
    public void shouldNotMovePostcodesIfAlreadyExistsInDestinationCourt() throws JsonProcessingException {
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

        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
        final List<String> duplicatedPostcodes = response.body().jsonPath().getList(".", String.class);
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
    public void shouldNotMoveInvalidPostcodes() throws JsonProcessingException {
        final String invalidPostcodesToMoveJson = objectMapper().writeValueAsString(POSTCODES_INVALID);
        final var response = doPutRequest(
            BIRMINGHAM_TO_WOLVERHAMPTON_COURT_POSTCODES_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            invalidPostcodesToMoveJson
        );

        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        final List<String> invalidPostcodes = response.body().jsonPath().getList(".", String.class);
        assertThat(invalidPostcodes).containsExactlyInAnyOrderElementsOf(POSTCODES_INVALID_RESPONSE);
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
}
