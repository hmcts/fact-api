package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContactTest {

    static uk.gov.hmcts.dts.fact.entity.Contact entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.Contact();
        entity.setName("A name");
        entity.setNumber("A number");
        entity.setExplanation("An explanation.");
        entity.setExplanationCy("An explanation in Welsh.");

    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        Contact contact = new Contact(entity);
        assertEquals(entity.getName(), contact.getName());
        assertEquals(entity.getNumber(), contact.getNumber());
        assertEquals(welsh ? entity.getExplanationCy() : entity.getExplanation(), contact.getExplanation());

        LocaleContextHolder.resetLocaleContext();
    }


}
