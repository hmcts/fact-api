package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.AdditionalLink;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
class AdminCourtAdditionalLinkEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String ADDITIONAL_LINK_PATH = "/additionalLinks";
    private static final String PLYMOUTH_COMBINED_COURT_SLUG = "plymouth-combined-court";
    private static final String PLYMOUTH_COMBINED_COURT_ADDITIONAL_LINK_PATH = ADMIN_COURTS_ENDPOINT
        + PLYMOUTH_COMBINED_COURT_SLUG + ADDITIONAL_LINK_PATH;
    private static final String COURT_NOT_FIND_PATH = ADMIN_COURTS_ENDPOINT + "plymou-combined-court" + ADDITIONAL_LINK_PATH;

    private static final String TEST_URL = "https://www.gov.uk/money-property-when-relationship-not-ends";
    private static final String TEST_DESCRIPTION = "Financial Remedy";
    private static final String TEST_DESCRIPTION_CY = "Rhwymedi Ariannol";


    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    void returnAdditionalLinksForTheCourt() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_ADDITIONAL_LINK_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<AdditionalLink> additionalLink = response.body().jsonPath().getList(".", AdditionalLink.class);
        assertThat(additionalLink).isNotEmpty();
    }

    @Test
    void shouldRequireATokenWhenGettingAdditionalLinksForTheCourt() {
        final Response response = doGetRequest(PLYMOUTH_COMBINED_COURT_ADDITIONAL_LINK_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingAdditionalLinksForTheCourt() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_ADDITIONAL_LINK_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldNotRetrieveAdditionalLinkWhenCourtSlugNotFound() {
        final var response = doGetRequest(COURT_NOT_FIND_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Update Request Tests. ***************************************************************/

    @Test
    void shouldUpdateAdditionalLink() throws JsonProcessingException {
        final List<AdditionalLink> currentAdditionalLink = getCurrentAdditionalLink();
        final List<AdditionalLink> expectedAdditionalLink = addNewAdditionalLink(currentAdditionalLink);
        final String updatedJson = objectMapper().writeValueAsString(expectedAdditionalLink);
        final String originalJson = objectMapper().writeValueAsString(currentAdditionalLink);

        final Response response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDITIONAL_LINK_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<AdditionalLink> updatedAdditionalLink = response.body().jsonPath().getList(".", AdditionalLink.class);
        assertThat(updatedAdditionalLink).containsExactlyElementsOf(expectedAdditionalLink);

        //clean up by removing added record
        final Response cleanUpResponse = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDITIONAL_LINK_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());

        final List<AdditionalLink> cleanAdditionalLink = cleanUpResponse.body().jsonPath().getList(".", AdditionalLink.class);
        assertThat(cleanAdditionalLink).containsExactlyElementsOf(currentAdditionalLink);
    }

    @Test
    void shouldRequireATokenWhenUpdatingAdditionalLinks() throws JsonProcessingException {
        final var response = doPutRequest(PLYMOUTH_COMBINED_COURT_ADDITIONAL_LINK_PATH, getTestAdditionalLink());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForUpdatingAdditionalLinks() throws JsonProcessingException {
        final var response = doPutRequest(
            PLYMOUTH_COMBINED_COURT_ADDITIONAL_LINK_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), getTestAdditionalLink()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldNotUpdateAdditionalLinkWhenCourtSlugNotFound() throws JsonProcessingException {
        final var response = doPutRequest(
            COURT_NOT_FIND_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            getTestAdditionalLink()
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Shared utility methods. ***************************************************************/

    private List<AdditionalLink> getCurrentAdditionalLink() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_ADDITIONAL_LINK_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        return response.body().jsonPath().getList(".", AdditionalLink.class);
    }

    private List<AdditionalLink> addNewAdditionalLink(final List<AdditionalLink> additionalLinks) {
        final List<AdditionalLink> updatedAdditionalLinks = new ArrayList<>(additionalLinks);
        updatedAdditionalLinks.add(new AdditionalLink(TEST_URL,TEST_DESCRIPTION,TEST_DESCRIPTION_CY));
        return updatedAdditionalLinks;
    }

    private static String getTestAdditionalLink() throws JsonProcessingException {
        final List<AdditionalLink> additionalLink = Arrays.asList(
            new AdditionalLink(TEST_URL,TEST_DESCRIPTION,TEST_DESCRIPTION_CY)
        );
        return objectMapper().writeValueAsString(additionalLink);
    }
}
