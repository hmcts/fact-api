package uk.gov.hmcts.dts.fact.admin.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.model.admin.ContactType;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.dts.fact.util.TestUtil.BEARER;
import static uk.gov.hmcts.dts.fact.util.TestUtil.objectMapper;

@SuppressWarnings("PMD.TooManyMethods")
class AdminContactTypeEndpointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_CONTACT_TYPE_ENDPOINT = "/admin/contactTypes/";
    private static final int CONTACT_TYPE_ID = 203;
    private static final String CONTACT_TYPE = "Test";
    private static final String CONTACT_TYPE_CY = "TestCy";
    private static final String CONTACT_TYPE_NEW_NAME = "NewTest";
    private static final String ADMIN_EMAIL_TYPES_ENDPOINT = "/admin/courts/emailTypes";


    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    void shouldReturnAllContactTypes() {
        final Response response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<ContactType> contactTypeList = response.body().jsonPath().getList(".", ContactType.class);
        assertThat(contactTypeList).hasSizeGreaterThan(1);

    }

    @Test
    void shouldRequireATokenWhenGettingAllContactTypes() {
        final Response response = doGetRequest(ADMIN_CONTACT_TYPE_ENDPOINT);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingAllContactTypes() {
        final Response response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    void shouldReturnContactType() {
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
    void shouldRequireATokenWhenGettingContactType() {
        final Response response = doGetRequest(ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldBeForbiddenForGettingContactType() {
        final Response response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldReturnContactTypeNotFound() {
        final Response response = doGetRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + "12345",
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    /************************************************************* Update Request Tests. ***************************************************************/

    @Test
    void shouldUpdateContactType() throws JsonProcessingException {
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
    void shouldBeForbiddenForUpdatingContactType() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        final String testJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPutRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenUpdatingContactType() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        final String testJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPutRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotFoundForUpdateWhenContactTypeDoesNotExist() throws JsonProcessingException {
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
    void shouldCreateContactType() throws JsonProcessingException {

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

        // asserting that email types list contains the newly added contact type
        final List<EmailType> emailTypeList = getCurrentEmailTypes();
        assertThat(emailTypeList).hasSizeGreaterThan(1);
        assertThat(emailTypeList).extracting(EmailType::getDescription).contains("NewTest");

        //clean up by removing added record
        final var cleanUpResponse = doDeleteRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + createdContactType.getId(),
            Map.of(AUTHORIZATION, BEARER + superAdminToken),""
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

        // asserting that email types list does not contains the newly added contact type after clean up
        final List<EmailType> cleanedEmailTypeList = getCurrentEmailTypes();
        assertThat(cleanedEmailTypeList).hasSizeGreaterThan(1);
        assertThat(cleanedEmailTypeList).extracting(EmailType::getDescription).doesNotContain("NewTest");
    }

    @Test
    void shouldBeForbiddenForCreatingContactType() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        final String testJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPostRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken), testJson
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenCreatingContactType() throws JsonProcessingException {
        final ContactType currentContactType = getCurrentContactType();
        final String testJson = objectMapper().writeValueAsString(currentContactType);

        final Response response = doPostRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT, testJson);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotCreateContactTypeThatAlreadyExist() throws JsonProcessingException {
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
    void adminShouldBeForbiddenForDeletingContactType() {

        final var response = doDeleteRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken), ""
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    void shouldRequireATokenWhenDeletingContactType() {
        final var response = doDeleteRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID,
            ""
        );
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotDeleteContactTypeNotFound() {

        final var response = doDeleteRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + 1234,
            Map.of(AUTHORIZATION, BEARER + superAdminToken), ""
        );
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    void shouldNotDeleteContactTypeInUse() {

        final var response = doDeleteRequest(
            ADMIN_CONTACT_TYPE_ENDPOINT + CONTACT_TYPE_ID,
            Map.of(AUTHORIZATION, BEARER + superAdminToken), ""
        );
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    /************************************************************* Shared utility methods. ***************************************************************/

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

    private List<EmailType> getCurrentEmailTypes() {
        final var response = doGetRequest(
            ADMIN_EMAIL_TYPES_ENDPOINT,
            Map.of(AUTHORIZATION, BEARER + superAdminToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());
        return response.body().jsonPath().getList(".", EmailType.class);
    }

    private ContactType createContactType() {
        return new ContactType(CONTACT_TYPE_ID,
                               CONTACT_TYPE_NEW_NAME,
                               CONTACT_TYPE_CY);
    }
}
