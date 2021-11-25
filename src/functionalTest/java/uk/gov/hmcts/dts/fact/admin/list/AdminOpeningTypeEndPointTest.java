package uk.gov.hmcts.dts.fact.admin.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@SuppressWarnings("PMD.TooManyMethods")
public class AdminOpeningTypeEndPointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_OPENING_TYPE_ENDPOINT = "/admin/openingTypes/";
    private static final int OPENING_TYPE_ID = 5;
    private static final String OPENING_TYPE = "Test";
    private static final String OPENING_TYPE_CY = "TestCy";
    private static final String OPENING_TYPE_NEW_NAME = "NewOpeningType";

    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    public void shouldReturnAllOpeningTypes() {
        final Response response = doGetRequest(
            ADMIN_OPENING_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<OpeningType> openingTypeList = response.body().jsonPath().getList(".", OpeningType.class);
        assertThat(openingTypeList).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingAllOpeningTypes() {
        final Response response = doGetRequest(ADMIN_OPENING_TYPE_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAllOpeningTypes() {
        final Response response = doGetRequest(
            ADMIN_OPENING_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    public void shouldReturnOpeningType() {
        final Response response = doGetRequest(
            ADMIN_OPENING_TYPE_ENDPOINT + OPENING_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());
        final OpeningType openingType = response.as(OpeningType.class);
        assertThat(openingType).isNotNull();
        assertThat(openingType.getId()).isEqualTo(OPENING_TYPE_ID);
    }

    @Test
    public void shouldRequireATokenWhenGettingOpeningType() {
        final Response response = doGetRequest(ADMIN_OPENING_TYPE_ENDPOINT + OPENING_TYPE_ID);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingOpeningType() {
        final Response response = doGetRequest(
            ADMIN_OPENING_TYPE_ENDPOINT + OPENING_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldReturnOpeningTypeNotFound() {
        final Response response = doGetRequest(
            ADMIN_OPENING_TYPE_ENDPOINT + "12345",
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Update Request Tests. ***************************************************************/

    @Test
    public void shouldUpdateOpeningType() throws JsonProcessingException {
        final OpeningType currentOpeningType = getCurrentOpeningType();
        final OpeningType expectedOpeningType = getUpdatedOpeningType();
        final String updatedJson = objectMapper().writeValueAsString(expectedOpeningType);
        final String originalJson = objectMapper().writeValueAsString(currentOpeningType);

        final Response response = doPutRequest(
            ADMIN_OPENING_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );

        assertThat(response.statusCode()).isEqualTo(OK.value());

        final OpeningType updatedOpeningType = response.as(OpeningType.class);
        assertThat(updatedOpeningType).isEqualTo(expectedOpeningType);

        //clean up by removing added record
        final Response cleanUpResponse = doPutRequest(
            ADMIN_OPENING_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        final OpeningType cleanOpeningType = cleanUpResponse.as(OpeningType.class);
        final String cleanUpJson = objectMapper().writeValueAsString(cleanOpeningType);
        assertThat(cleanUpJson).isEqualTo(originalJson);
    }

    @Test
    public void shouldBeForbiddenForUpdatingOpeningType() throws JsonProcessingException {
        final OpeningType currentOpeningType = getCurrentOpeningType();
        final String testJson = objectMapper().writeValueAsString(currentOpeningType);

        final Response response = doPutRequest(
            ADMIN_OPENING_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenUpdatingOpeningType() throws JsonProcessingException {
        final OpeningType currentOpeningType = getCurrentOpeningType();
        final String testJson = objectMapper().writeValueAsString(currentOpeningType);

        final Response response = doPutRequest(
            ADMIN_OPENING_TYPE_ENDPOINT, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotFoundForUpdateWhenOpeningTypeDoesNotExist() throws JsonProcessingException {
        final OpeningType currentOpeningType = getCurrentOpeningType();
        currentOpeningType.setId(123);
        final String testJson = objectMapper().writeValueAsString(currentOpeningType);

        final Response response = doPutRequest(
            ADMIN_OPENING_TYPE_ENDPOINT, Map.of(AUTHORIZATION, BEARER + superAdminToken),
            testJson
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Delete request tests section. ***************************************************************/

    @Test
    public void adminShouldBeForbiddenForDeletingOpeningType() {

        final var response = doDeleteRequest(
            ADMIN_OPENING_TYPE_ENDPOINT + OPENING_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), ""
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenDeletingOpeningType() {
        final var response = doDeleteRequest(
            ADMIN_OPENING_TYPE_ENDPOINT + OPENING_TYPE_ID,
            ""
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotDeleteOpeningTypeNotFound() {

        final var response = doDeleteRequest(
            ADMIN_OPENING_TYPE_ENDPOINT + 123,
            Map.of(AUTHORIZATION, BEARER + superAdminToken), ""
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void shouldNotDeleteOpeningTypeInUse() {

        final var response = doDeleteRequest(
            ADMIN_OPENING_TYPE_ENDPOINT + OPENING_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + superAdminToken), ""
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    /************************************************************* POST request tests section. ***************************************************************/

    @Test
    public void shouldCreateOpeningType() throws JsonProcessingException {

        final List<OpeningType> currentOpeningType = getCurrentOpeningTypes();
        final OpeningType expectedOpeningType = createOpeningType();
        final String newOpeningTypesJson = objectMapper().writeValueAsString(expectedOpeningType);

        final var response = doPostRequest(
            ADMIN_OPENING_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newOpeningTypesJson
        );
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        final OpeningType createdOpeningType = response.as(OpeningType.class);

        assertThat(createdOpeningType.getType()).isEqualTo(expectedOpeningType.getType());

        //clean up by removing added record
        final var cleanUpResponse = doDeleteRequest(
            ADMIN_OPENING_TYPE_ENDPOINT + createdOpeningType.getId(),
            Map.of(AUTHORIZATION, BEARER + superAdminToken),""
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());

        final var responseAfterClean = doGetRequest(
            ADMIN_OPENING_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );

        final List<OpeningType> cleanedOpeningType = responseAfterClean.body().jsonPath().getList(
            ".",
            OpeningType.class
        );
        assertThat(cleanedOpeningType).containsExactlyElementsOf(currentOpeningType);
    }

    @Test
    public void shouldBeForbiddenForCreatingOpeningType() throws JsonProcessingException {
        final OpeningType currentOpeningType = getCurrentOpeningType();
        final String testJson = objectMapper().writeValueAsString(currentOpeningType);

        final Response response = doPostRequest(
            ADMIN_OPENING_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenCreatingOpeningType() throws JsonProcessingException {
        final OpeningType currentOpeningType = getCurrentOpeningType();
        final String testJson = objectMapper().writeValueAsString(currentOpeningType);

        final Response response = doPostRequest(
            ADMIN_OPENING_TYPE_ENDPOINT, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotCreateOpeningTypeThatAlreadyExist() throws JsonProcessingException {
        final OpeningType currentOpeningType = getCurrentOpeningType();
        final String testJson = objectMapper().writeValueAsString(currentOpeningType);

        final Response response = doPostRequest(
            ADMIN_OPENING_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    /************************************************************* Shared utility methods. ***************************************************************/

    private OpeningType getCurrentOpeningType() {
        final Response response = doGetRequest(
            ADMIN_OPENING_TYPE_ENDPOINT + OPENING_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        return response.as(OpeningType.class);
    }

    private OpeningType getUpdatedOpeningType() {
        return new OpeningType(OPENING_TYPE_ID,
                               OPENING_TYPE,
                               OPENING_TYPE_CY);
    }

    private List<OpeningType> getCurrentOpeningTypes() {
        final var response = doGetRequest(
            ADMIN_OPENING_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        return response.body().jsonPath().getList(".", OpeningType.class);
    }

    private OpeningType createOpeningType() {
        return new OpeningType(OPENING_TYPE_ID,
                               OPENING_TYPE_NEW_NAME,
                               OPENING_TYPE_CY);
    }
}
