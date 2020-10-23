package uk.gov.hmcts.dts.fact.util;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

public class UtilsTest {

    protected static final String ENGLISH = "english";
    private static final String EXPLANATION_OF_CONTACT = "Explanation of contact";
    protected static final String WELSH = "welsh";

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
        List<String> dxNumbers = contacts.stream().filter(Utils.nameIsDX).map(Contact::getNumber).collect(toList());

        assertEquals("123", dxNumbers.get(0));
        assertThat(dxNumbers.size()).isEqualTo(1);
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

        List<Contact> contactsWithoutDx = contacts.stream().filter(Utils.nameIsNotDX).collect(toList());
        assertEquals(contactsWithoutDx.get(0).getName(), contact2.getName());
        assertThat(contactsWithoutDx.size()).isEqualTo(1);
    }

    @Test
    void testHtmlFilter() {
        String text = "<p>Text that needs html stripping</p>";
        assertEquals("Text that needs html stripping", Utils.stripHtmlFromString(text));
    }

    @Test
    void shouldReturnEnglish() {
        assertEquals(ENGLISH, chooseString(WELSH, ENGLISH));
    }

    @Test
    void shouldReturnEnglishIfWelshNull() {
        LocaleContextHolder.setLocale(new Locale("cy"));
        assertEquals(ENGLISH, chooseString(null, ENGLISH));
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void shouldReturnEnglishIfWelshBlank() {
        LocaleContextHolder.setLocale(new Locale("cy"));
        assertEquals(ENGLISH, chooseString(" ", ENGLISH));
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void shouldReturnWelsh() {
        LocaleContextHolder.setLocale(new Locale("cy"));
        assertEquals(WELSH, chooseString(WELSH, ENGLISH));
        LocaleContextHolder.resetLocaleContext();
    }


}
