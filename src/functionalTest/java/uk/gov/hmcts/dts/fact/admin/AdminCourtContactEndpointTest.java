package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.admin.Contact;
import uk.gov.hmcts.dts.fact.model.admin.ContactType;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
public class AdminCourtContactEndpointTest extends AdminFunctionalTestBase {
    private static final String CONTACTS_PATH = "/" + "contacts";
    private static final String CONTACT_TYPES_PATH = "contactTypes";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG = "birmingham-civil-and-family-justice-centre";
    private static final int ENQUIRIES_CONTACT_TYPE_ID = 22;
    private static final String ENQUIRIES_CONTACT_TYPE = "Enquiries";
    private static final String ENQUIRIES_CONTACT_TYPE_WELSH = "Ymholiadau";
    private static final String TEST_NUMBER = "test number";
    private static final Contact TEST_CONTACT = new Contact(ENQUIRIES_CONTACT_TYPE_ID, TEST_NUMBER, "explanation", "explanation cy");

    @Test
    public void shouldRetrieveContacts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + CONTACTS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<Contact> contacts = response.body().jsonPath().getList(".", Contact.class);
        assertThat(contacts).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenRetrievingContacts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + CONTACTS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForRetrievingContacts() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + CONTACTS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldAddOrRemoveContacts() throws JsonProcessingException {
        // Add a new contact
        final List<Contact> currentContacts = getCurrentContacts(BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG);
        List<Contact> expectedContacts = addNewContact(currentContacts);
        String json = objectMapper().writeValueAsString(expectedContacts);

        var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(json)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + CONTACTS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        List<Contact> updatedContacts = response.body().jsonPath().getList(".", Contact.class);
        assertThat(updatedContacts).containsExactlyElementsOf(expectedContacts);

        // Check the standard court endpoint still display the added contact type in English after the contact update
        response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());

        Court court = response.as(Court.class);
        List<uk.gov.hmcts.dts.fact.model.Contact> contacts = court.getContacts();
        assertThat(contacts).hasSizeGreaterThan(1);
        assertThat(contacts.stream()).anyMatch(contact -> contact.getNumber().equals(TEST_NUMBER)
            && contact.getName().equals(ENQUIRIES_CONTACT_TYPE));

        // Check the standard court endpoint still display the added contact type in Welsh after the contact update
        response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(ACCEPT_LANGUAGE, "cy")
            .when()
            .get(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());

        court = response.as(Court.class);
        contacts = court.getContacts();
        assertThat(contacts).hasSizeGreaterThan(1);
        assertThat(contacts.stream()).anyMatch(contact -> contact.getNumber().equals(TEST_NUMBER)
            && contact.getName().equals(ENQUIRIES_CONTACT_TYPE_WELSH));

        // Remove the added contact
        expectedContacts = removeContact(updatedContacts);
        json = objectMapper().writeValueAsString(expectedContacts);

        response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .body(json)
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + CONTACTS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        updatedContacts = response.body().jsonPath().getList(".", Contact.class);
        assertThat(updatedContacts).containsExactlyElementsOf(expectedContacts);
    }

    @Test
    public void shouldRequireATokenWhenUpdatingContacts() throws JsonProcessingException {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .body(getTestContactsJson())
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + CONTACTS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForUpdatingContacts() throws JsonProcessingException {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .body(getTestContactsJson())
            .when()
            .put(ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + CONTACTS_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRetrieveAllContactTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + CONTACT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final List<ContactType> contactTypes = response.body().jsonPath().getList(".", ContactType.class);
        assertThat(contactTypes).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenRetrievingAllContactTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + CONTACT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForRetrievingContactTypes() {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + forbiddenToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + CONTACT_TYPES_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    private List<Contact> getCurrentContacts(final String slug) {
        final var response = given()
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .header(AUTHORIZATION, BEARER + authenticatedToken)
            .when()
            .get(ADMIN_COURTS_ENDPOINT + slug + CONTACTS_PATH)
            .thenReturn();

        return response.body().jsonPath().getList(".", Contact.class);
    }

    private List<Contact> addNewContact(List<Contact> contacts) {
        List<Contact> updatedContacts = new ArrayList<>(contacts);
        updatedContacts.add(TEST_CONTACT);
        return updatedContacts;
    }

    private List<Contact> removeContact(List<Contact> contacts) {
        List<Contact> updatedContacts = new ArrayList<>(contacts);
        updatedContacts.removeIf(time -> time.getNumber().equals(TEST_NUMBER));
        return updatedContacts;
    }

    private static String getTestContactsJson() throws JsonProcessingException {
        final List<Contact> contacts = Arrays.asList(
            new Contact(2, "0207123456", "explanation 1", "explanation cy 1"),
            new Contact(5, "0207234567", "explanation 2", "explanation cy 2"),
            new Contact(9, "0207345678", "explanation 3", "explanation cy 3")
        );
        return objectMapper().writeValueAsString(contacts);
    }
}
