package uk.gov.hmcts.dts.fact.util;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.model.Contact;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FiltersTest {

    private static final String EXPLANATION_OF_CONTACT = "Explanation of contact";

    @Test
    void testDxExtractContacts() {
        Contact contact1 = new Contact();
        contact1.setName("DX");
        contact1.setNumber("123");
        contact1.setExplanation(EXPLANATION_OF_CONTACT);

        Contact contact2 = new Contact();
        contact2.setName("Name of contact");
        contact2.setNumber("456");
        contact2.setExplanation(EXPLANATION_OF_CONTACT);

        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact1);
        contacts.add(contact2);
        List<String> dxNumber = Filters.extractDxContacts(contacts);

        assertEquals("123", dxNumber.get(0));
        assertEquals(contacts.get(0), contact1);
        assertEquals(contacts.get(1), contact2);
    }

    @Test
    void testRemoveDxContacts() {
        Contact contact1 = new Contact();
        contact1.setName("DX");
        contact1.setNumber("123");
        contact1.setExplanation(EXPLANATION_OF_CONTACT);

        Contact contact2 = new Contact();
        contact2.setName("Name of contact");
        contact2.setNumber("456");
        contact2.setExplanation(EXPLANATION_OF_CONTACT);

        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact1);
        contacts.add(contact2);

        List<Contact> contactsWithoutDx = Filters.removeDxContacts(contacts);

        assertEquals(contactsWithoutDx.get(0).getName(), contact2.getName());
    }

    @Test
    void testHtmlFilter() {
        String text = "<p>Text that needs html stripping</p>";
        assertEquals("Text that needs html stripping", Filters.stripHtmlFromString(text));
    }
}
