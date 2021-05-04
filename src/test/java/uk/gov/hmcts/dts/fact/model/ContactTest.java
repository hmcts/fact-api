package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.ContactType;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ContactTest {

    private static uk.gov.hmcts.dts.fact.entity.Contact entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.Contact();
        entity.setName("A name");
        entity.setNameCy("A name but in Welsh");
        entity.setNumber("A number");
        entity.setExplanation("An explanation.");
        entity.setExplanationCy("An explanation in Welsh.");

    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            final Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        final Contact contact = new Contact(entity);
        assertEquals(entity.getNumber(), contact.getNumber());
        assertEquals(welsh ? entity.getNameCy() : entity.getName(), contact.getName());
        assertEquals(welsh ? entity.getExplanationCy() : entity.getExplanation(), contact.getExplanation());

        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void testCreationWithoutContactType() {
        LocaleContextHolder.setLocale(new Locale("en"));
        entity.setContactType(null);

        final Contact contact = new Contact(entity);
        assertThat(contact.getName()).isEqualTo(entity.getName());
        assertThat(contact.getExplanation()).isEqualTo(entity.getExplanation());
    }

    @Test
    void testCreationWithContactType() {
        LocaleContextHolder.setLocale(new Locale("en"));
        final ContactType contactType = new ContactType(1, "A name within type", null);
        entity.setContactType(contactType);

        Contact contact = new Contact(entity);
        assertThat(contact.getName()).isEqualTo(entity.getContactType().getName());
        assertThat(contact.getExplanation()).isEqualTo(entity.getExplanation());
    }

}
