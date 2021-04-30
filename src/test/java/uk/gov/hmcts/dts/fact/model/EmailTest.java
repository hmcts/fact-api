package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailTest {

    private static uk.gov.hmcts.dts.fact.entity.Email entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.Email();
        entity.setAddress("test@test.com");
        entity.setDescription("An email address");
        entity.setDescriptionCy("An email address but in Welsh");
        entity.setExplanation("You email it.");
        entity.setExplanationCy("You email it in Welsh.");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        Email email = new Email(entity);
        assertEquals(entity.getAddress(), email.getAddress());
        assertEquals(welsh ? entity.getExplanationCy() : entity.getExplanation(), email.getExplanation());
        assertEquals(welsh ? entity.getDescriptionCy() : entity.getDescription(), email.getDescription());

        LocaleContextHolder.resetLocaleContext();
    }

}
