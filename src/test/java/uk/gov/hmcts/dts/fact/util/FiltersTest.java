package uk.gov.hmcts.dts.fact.util;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.model.Contact;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FiltersTest {

    @Test
    void testDxFilter() {
        Contact contact = new Contact();
        contact.setName("DX");
        contact.setNumber("123");
        contact.setExplanation("Explanation of contact");

        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);
        List<String> dxNumbers = Filters.filterDxNumber(contacts);

        assertEquals("123", dxNumbers.get(0));
    }

    @Test
    void testHtmlFilter() {
        String text = "<p>Text that needs html stripping</p>";
        assertEquals("Text that needs html stripping", Filters.stripHtmlFromString(text));
    }
}
