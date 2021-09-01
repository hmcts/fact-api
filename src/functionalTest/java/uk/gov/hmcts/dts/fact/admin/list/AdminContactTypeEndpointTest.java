package uk.gov.hmcts.dts.fact.admin.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.model.admin.ContactType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@SuppressWarnings("PMD.TooManyMethods")
public class AdminContactTypeEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_CONTACT_TYPE_ENDPOINT = "/admin/contactTypes/";
    private static final int CONTACT_TYPE_ID = 203;
    private static final String CONTACT_TYPE = "Test";
    private static final String CONTACT_TYPE_CY = "TestCy";
    private static final String CONTACT_TYPE_NEW_NAME = "NewTest";

    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    public void shouldReturnAllContactTypes() {
        final Response response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<ContactType> contactTypeList = response.body().jsonPath().getList(".", ContactType.class);
        assertThat(contactTypeList).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenGettingAllContactTypes() {
        final Response response = doGetRequest(ADMIN_CONTACT_TYPE_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingAllContactTypes() {
        final Response response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }
    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    public void shouldReturnContactType() {
        final Response response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());
        final ContactType contactType = response.as(ContactType.class);
        assertThat(contactType).isNotNull();
        assertThat(contactType.getId()).isEqualTo(CONTACT_TYPE_ID);
    }

    @Test
    public void shouldRequireATokenWhenGettingContactType() {
        final Response response = doGetRequest(ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingContactType() {
        final Response response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldReturnContactTypeNotFound() {
        final Response response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + "12345",
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }
    /************************************************************* Update Request Tests. ***************************************************************/

    @Test
    public void shouldUpdateContactType() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        final ContactType expectedContactType = getUpdatedContactType();
        final String updatedJson = objectMapper().writeValueAsString(expectedContactType);
        final String originalJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPutRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            updatedJson
        );

        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ContactType updatedContactType = response.as(ContactType.class);
        assertThat(updatedContactType).isEqualTo(expectedContactType);

        //clean up by removing added record
        final Response cleanUpResponse = doPutRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            originalJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());
        final ContactType cleanContactType = cleanUpResponse.as(ContactType.class);
        final String cleanUpJson = objectMapper().writeValueAsString(cleanContactType);
        assertThat(cleanUpJson).isEqualTo(originalJson);
    }

    @Test
    public void shouldBeForbiddenForUpdatingContactType() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        final String testJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPutRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenUpdatingContactType() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        final String testJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPutRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotFoundForUpdateWhenContactTypeDoesNotExist() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        currentContactType.setId(1234);
        final String testJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPutRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT, Map.of(AUTHORIZATION, BEARER + superAdminToken),
            testJson
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* POST request tests section. ***************************************************************/

    @Test
    public void shouldCreateContactType() throws JsonProcessingException {

        final List<ContactType> currentContactType = getCurrentContactTypes();
        final ContactType expectedContactType = createContactType();
        final String newContactTypesJson = objectMapper().writeValueAsString(expectedContactType);

        final var response = doPostRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newContactTypesJson
        );
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        final ContactType createdContactType = response.as(ContactType.class);

        assertThat(createdContactType.getType()).isEqualTo(expectedContactType.getType());
        final String deleteJson = objectMapper().writeValueAsString(createdContactType);

        //clean up by removing added record
        final var cleanUpResponse = doDeleteRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + createdContactType.getId(),
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            deleteJson
        );
        assertThat(cleanUpResponse.statusCode()).isEqualTo(OK.value());

        final var responseAfterClean = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );

        final List<ContactType> cleanedContactType = responseAfterClean.body().jsonPath().getList(
            ".",
            ContactType.class
        );
        assertThat(cleanedContactType).containsExactlyElementsOf(currentContactType);
    }

    @Test
    public void shouldBeForbiddenForCreatingContactType() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        final String testJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPostRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenCreatingContactType() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        final String testJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPostRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotCreateContactTypeThatAlreadyExist() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        final String testJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPostRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    /************************************************************* Delete request tests section. ***************************************************************/

    @Test
    public void adminShouldBeForbiddenForDeletingContactType() throws JsonProcessingException {

        final var response = doDeleteRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), getTestContactType()
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRequireATokenWhenDeletingContactType() throws JsonProcessingException {
        final var response = doDeleteRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID,
            getTestContactType()
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldNotDeleteContactTypeNotFound() throws JsonProcessingException {

        final var response = doDeleteRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + 1234,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            getTestContactType()
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void shouldNotDeleteContactTypeInUse() throws JsonProcessingException {

        final var response = doDeleteRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            getTestContactType()
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    /************************************************************* Shared utility methods. ***************************************************************/
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")

    private ContactType getCurrentContactType() {
        final Response response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        return response.as(ContactType.class);
    }

    private ContactType getUpdatedContactType() {

        return new ContactType(CONTACT_TYPE_ID,
                               CONTACT_TYPE,
                               CONTACT_TYPE_CY);
    }

    private List<ContactType> getCurrentContactTypes() {
        final var response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        return response.body().jsonPath().getList(".", ContactType.class);
    }

    private String getTestContactType() throws JsonProcessingException {
        return objectMapper().writeValueAsString(getUpdatedContactType());
    }

    private ContactType createContactType() {
        return new ContactType(CONTACT_TYPE_ID,
                               CONTACT_TYPE_NEW_NAME,
                               CONTACT_TYPE_CY);
    }
}
