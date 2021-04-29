package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.EmailType;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailTest {

    static uk.gov.hmcts.dts.fact.entity.Email entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.Email();
        entity.setAddress("test@test.com");
        entity.setDescription("An email address");
        entity.setDescriptionCy("An email address but in Welsh");
        entity.setExplanation("You email it.");
        entity.setExplanationCy("You email it in Welsh.");
    }

    @Test
    void testEmailTypeConstructor() {
        EmailType emailType = new EmailType(1, "desc", "desc cy");
        uk.gov.hmcts.dts.fact.entity.Email email = new uk.gov.hmcts.dts.fact.entity.Email(
            "address", "expl", "expl cy", emailType);
        assertEquals(email.getAdminEmailType(), emailType);
        assertEquals(email.getExplanation(), "expl");
        assertEquals(email.getExplanationCy(), "expl cy");
        assertEquals(email.getAddress(), "address");
        assertEquals(email.getDescription(), "");
        assertNull(email.getDescriptionCy());
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
