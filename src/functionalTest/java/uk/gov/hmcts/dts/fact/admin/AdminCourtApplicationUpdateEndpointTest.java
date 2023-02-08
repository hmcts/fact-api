package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.ADMIN_COURTS_ENDPOINT;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@ExtendWith(SpringExtension.class)
class AdminCourtApplicationUpdateEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String DIVORCE_SERVICE_CENTRE_SLUG = "divorce-service-centre";
    private static final String APPLICATION_PROGRESSION_PATH = "/application-progression";
    private static final String APPLICATION_PROGRESSION_OPTION_FULL_PATH = ADMIN_COURTS_ENDPOINT + DIVORCE_SERVICE_CENTRE_SLUG
        + APPLICATION_PROGRESSION_PATH;
    private static final String APPLICATION_PROGRESSION_NOT_FOUND_PATH = ADMIN_COURTS_ENDPOINT + "NotFound" + APPLICATION_PROGRESSION_PATH;
    private static final String TEST_TYPE = "test type";
    private static final String TEST_TYPE_CY = "test type welsh";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_EXTERNAL_LINK = "www.test.com";
    private static final String TEST_EXTERNAL_LINK_DESCRIPTION = "test description";
    private static final String TEST_EXTERNAL_LINK_DESCRIPTION_CY = "test description welsh";

    @Test
    void shouldGetApplicationUpdateTypes() {
        final var response = doGetRequest(APPLICATION_PROGRESSION_OPTION_FULL_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<ApplicationUpdate> applicationUpdates = response.body().jsonPath().getList(".", ApplicationUpdate.class);
        assertThat(applicationUpdates).isNotEmpty();
    }

    @Test
    void shouldRequireATokenWhenGettingApplicationUpdateTypes() {
        final var response = doGetRequest(APPLICATION_PROGRESSION_OPTION_FULL_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingApplicationUpdateTypes() {
        final var response = doGetRequest(
            APPLICATION_PROGRESSION_OPTION_FULL_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldReturnNotFoundWhenServiceCentreDoesNotExist() {
        final Response response = doGetRequest(
            ADMIN_COURTS_ENDPOINT + "test"
                + APPLICATION_PROGRESSION_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Update Request Tests. ***************************************************************/

    @Test
    void shouldUpdateApplicationUpdatesTypes() throws JsonProcessingException {
        final List<ApplicationUpdate> currentApplicationUpdateTypes = getCurrentApplicationUpdateTypes();
        final List<ApplicationUpdate> expectedApplicationUpdateTypes = addNewApplicationUpdateType(currentApplicationUpdateTypes);
        final String updatedJson = objectMapper().writeValueAsString(expectedApplicationUpdateTypes);
        final String originalJson = objectMapper().writeValueAsString(currentApplicationUpdateTypes);

        final Response response = doPutRequest(
            APPLICATION_PROGRESSION_OPTION_FULL_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );

        final List<ApplicationUpdate> updatedApplicationUpdateType = response.body().jsonPath().getList(".", ApplicationUpdate.class);
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(updatedApplicationUpdateType).containsExactlyElementsOf(expectedApplicationUpdateTypes);

        //clean up by removing added record
        final Response cleanUpResponse = doPutRequest(
            APPLICATION_PROGRESSION_OPTION_FULL_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());

        final List<ApplicationUpdate> cleanApplicationUpdateType = cleanUpResponse.body().jsonPath().getList(".", ApplicationUpdate.class);
        assertThat(cleanApplicationUpdateType).containsExactlyElementsOf(currentApplicationUpdateTypes);

    }

    @Test
    void shouldRequireATokenWhenUpdatingApplicationUpdatesTypes() throws JsonProcessingException {

        final List<ApplicationUpdate> currentApplicationUpdateTypes = getCurrentApplicationUpdateTypes();
        final String originalJson = objectMapper().writeValueAsString(currentApplicationUpdateTypes);
        final var response = doPutRequest(APPLICATION_PROGRESSION_OPTION_FULL_PATH, originalJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForUpdatingApplicationUpdatesTypes() throws JsonProcessingException {

        final List<ApplicationUpdate> currentApplicationUpdateTypes = getCurrentApplicationUpdateTypes();
        final String originalJson = objectMapper().writeValueAsString(currentApplicationUpdateTypes);
        final var response = doPutRequest(
            APPLICATION_PROGRESSION_OPTION_FULL_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), originalJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldnotUpdateAndReturnNotFoundWhenCourtDoesNotExist() throws JsonProcessingException {

        final List<ApplicationUpdate> currentApplicationUpdateTypes = getCurrentApplicationUpdateTypes();
        final String originalJson = objectMapper().writeValueAsString(currentApplicationUpdateTypes);
        final Response response = doPutRequest(
            APPLICATION_PROGRESSION_NOT_FOUND_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken),
            originalJson
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Shared utility methods. ***************************************************************/

    private List<ApplicationUpdate> getCurrentApplicationUpdateTypes() {
        final Response response = doGetRequest(
            APPLICATION_PROGRESSION_OPTION_FULL_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        return response.body().jsonPath().getList(".", ApplicationUpdate.class);
    }

    private List<ApplicationUpdate> addNewApplicationUpdateType(final List<ApplicationUpdate> applicationUpdates) {
        final List<ApplicationUpdate> updatedApplicationUpdateType = new ArrayList<>(applicationUpdates);
        updatedApplicationUpdateType.add(new ApplicationUpdate(TEST_TYPE,TEST_TYPE_CY,TEST_EMAIL,TEST_EXTERNAL_LINK,TEST_EXTERNAL_LINK_DESCRIPTION,TEST_EXTERNAL_LINK_DESCRIPTION_CY));
        return updatedApplicationUpdateType;
    }

}

