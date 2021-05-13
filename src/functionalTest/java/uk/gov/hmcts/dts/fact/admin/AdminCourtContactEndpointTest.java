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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
@SuppressWarnings("PMD.TooManyMethods")
public class AdminCourtContactEndpointTest extends AdminFunctionalTestBase {
    private static final String CONTACTS_PATH = "/" + "contacts";
    private static final String CONTACT_TYPES_FULL_PATH = ADMIN_COURTS_ENDPOINT + "contactTypes";
    private static final String BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG = "birmingham-civil-and-family-justice-centre";
    private static final String BIRMINGHAM_CONTACTS_PATH = ADMIN_COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG + CONTACTS_PATH;
    private static final String TEST_NUMBER = "test number";
    private static final Contact TEST_CONTACT = new Contact(null, TEST_NUMBER, "explanation", "explanation cy", true);
    private static final String FAX_TYPE_SUFFIX = " fax";

    @Test
    public void shouldRetrieveContacts() {
        final var response = doGetRequest(BIRMINGHAM_CONTACTS_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<Contact> contacts = response.body().jsonPath().getList(".", Contact.class);
        assertThat(contacts).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenRetrievingContacts() {
        final var response = doGetRequest(BIRMINGHAM_CONTACTS_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForRetrievingContacts() {
        final var response = doGetRequest(BIRMINGHAM_CONTACTS_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldAddAndRemoveContacts() throws JsonProcessingException {
        var response = doGetRequest(BIRMINGHAM_CONTACTS_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        final List<Contact> currentContacts = response.body().jsonPath().getList(".", Contact.class);

        // Add a new contact
        List<Contact> expectedContacts = addNewContact(currentContacts);
        String json = objectMapper().writeValueAsString(expectedContacts);

        response = doPutRequest(BIRMINGHAM_CONTACTS_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), json);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        List<Contact> updatedContacts = response.body().jsonPath().getList(".", Contact.class);
        assertThat(updatedContacts).containsExactlyElementsOf(expectedContacts);

        verifyCourtContactsAfterUpdate();

        // Remove the added contact
        expectedContacts = removeContact(updatedContacts);
        json = objectMapper().writeValueAsString(expectedContacts);

        response = doPutRequest(BIRMINGHAM_CONTACTS_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), json);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        updatedContacts = response.body().jsonPath().getList(".", Contact.class);
        assertThat(updatedContacts).containsExactlyElementsOf(expectedContacts);
    }

    @Test
    public void shouldChangePhoneNumberToFaxAndViceVersa() throws JsonProcessingException {
        var response = doGetRequest(BIRMINGHAM_CONTACTS_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        final List<Contact> currentContacts = response.body().jsonPath().getList(".", Contact.class);

        // Update a contact from phone number to fax
        List<Contact> expectedContacts = updateAPhoneNumberContactToFax(currentContacts);
        String json = objectMapper().writeValueAsString(expectedContacts);

        response = doPutRequest(BIRMINGHAM_CONTACTS_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), json);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        List<Contact> updatedContacts = response.body().jsonPath().getList(".", Contact.class);
        assertThat(updatedContacts).containsExactlyElementsOf(expectedContacts);

        // Check the fax contact type is shown in the standard court object
        response = doGetRequest(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        Court court = response.as(Court.class);
        List<uk.gov.hmcts.dts.fact.model.Contact> contacts = court.getContacts();
        assertThat(contacts).hasSizeGreaterThan(1);
        assertThat(contacts.get(0).getName()).endsWith(FAX_TYPE_SUFFIX);

        // Update a contact from fax to phone number
        expectedContacts = updateAFaxContactToPhoneNumber(updatedContacts);
        json = objectMapper().writeValueAsString(expectedContacts);

        response = doPutRequest(BIRMINGHAM_CONTACTS_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken), json);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        updatedContacts = response.body().jsonPath().getList(".", Contact.class);
        assertThat(updatedContacts).containsExactlyElementsOf(expectedContacts);

        // Check the fax contact type is not shown in the standard court object
        response = doGetRequest(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        court = response.as(Court.class);
        contacts = court.getContacts();
        assertThat(contacts).hasSizeGreaterThan(1);
        assertThat(contacts.get(0).getName()).doesNotEndWith(FAX_TYPE_SUFFIX);
    }

    @Test
    public void shouldRequireATokenWhenUpdatingContacts() throws JsonProcessingException {
        final var response = doPutRequest(BIRMINGHAM_CONTACTS_PATH, getTestContactsJson());
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForUpdatingContacts() throws JsonProcessingException {
        final var response = doPutRequest(BIRMINGHAM_CONTACTS_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken), getTestContactsJson());
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    public void shouldRetrieveAllContactTypes() {
        final var response = doGetRequest(CONTACT_TYPES_FULL_PATH, Map.of(AUTHORIZATION, BEARER + authenticatedToken));
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<ContactType> contactTypes = response.body().jsonPath().getList(".", ContactType.class);
        assertThat(contactTypes).hasSizeGreaterThan(1);
    }

    @Test
    public void shouldRequireATokenWhenRetrievingAllContactTypes() {
        final var response = doGetRequest(CONTACT_TYPES_FULL_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForRetrievingContactTypes() {
        final var response = doGetRequest(CONTACT_TYPES_FULL_PATH, Map.of(AUTHORIZATION, BEARER + forbiddenToken));
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    private void verifyCourtContactsAfterUpdate() {
        var response = doGetRequest(COURTS_ENDPOINT + BIRMINGHAM_CIVIL_AND_FAMILY_JUSTICE_CENTRE_SLUG);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        Court court = response.as(Court.class);
        List<uk.gov.hmcts.dts.fact.model.Contact> contacts = court.getContacts();
        assertThat(contacts).hasSizeGreaterThan(1);
        assertThat(contacts.stream()).anyMatch(contact -> contact.getNumber().equals(TEST_NUMBER));
    }

    private List<Contact> addNewContact(final List<Contact> contacts) {
        final List<Contact> updatedContacts = new ArrayList<>(contacts);
        updatedContacts.add(TEST_CONTACT);
        return updatedContacts;
    }

    private List<Contact> removeContact(final List<Contact> contacts) {
        final List<Contact> updatedContacts = new ArrayList<>(contacts);
        updatedContacts.removeIf(time -> time.getNumber().equals(TEST_NUMBER));
        return updatedContacts;
    }

    private List<Contact> updateAPhoneNumberContactToFax(final List<Contact> contacts) {
        final List<Contact> updatedContacts = new ArrayList<>(contacts);
        updatedContacts.get(0).setFax(true);
        return updatedContacts;
    }

    private List<Contact> updateAFaxContactToPhoneNumber(final List<Contact> contacts) {
        final List<Contact> updatedContacts = new ArrayList<>(contacts);
        updatedContacts.get(0).setFax(false);
        return updatedContacts;
    }

    private static String getTestContactsJson() throws JsonProcessingException {
        final List<Contact> contacts = Arrays.asList(
            new Contact(2, "0207123456", "explanation 1", "explanation cy 1", false),
            new Contact(5, "0207234567", "explanation 2", "explanation cy 2", false),
            new Contact(9, "0207345678", "explanation 3", "explanation cy 3", true)
        );
        return objectMapper().writeValueAsString(contacts);
    }
}
