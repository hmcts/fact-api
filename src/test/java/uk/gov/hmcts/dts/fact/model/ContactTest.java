package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.ContactType;

import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ContactTest {
    private static final String DESCRIPTION_IN_CONTACT_TABLE = "Contact description";
    private static final String DESCRIPTION_CY_IN_CONTACT_TABLE = "Contact description cy";
    private static final String DESCRIPTION_IN_ADMIN_TABLE = "Admin description";
    private static final String DESCRIPTION_CY_IN_ADMIN_TABLE = "Admin description cy";
    private static final String FAX = "Fax";
    private static final String FAX_CY = "Ffacs";

    private static uk.gov.hmcts.dts.fact.entity.Contact entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.Contact();
        entity.setName(DESCRIPTION_IN_CONTACT_TABLE);
        entity.setNameCy(DESCRIPTION_CY_IN_CONTACT_TABLE);
        entity.setNumber("A number");
        entity.setExplanation("An explanation.");
        entity.setExplanationCy("An explanation in Welsh.");

    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForTestCreationWithoutContactType() {
        return Stream.of(
            Arguments.of(true, false, FAX, FAX_CY, FAX),
            Arguments.of(true, false, DESCRIPTION_IN_CONTACT_TABLE, DESCRIPTION_CY_IN_CONTACT_TABLE, DESCRIPTION_IN_CONTACT_TABLE + " fax"),
            Arguments.of(false, false, DESCRIPTION_IN_CONTACT_TABLE, DESCRIPTION_CY_IN_CONTACT_TABLE, DESCRIPTION_IN_CONTACT_TABLE),
            Arguments.of(true, true, FAX, FAX_CY, FAX_CY),
            Arguments.of(true, true, DESCRIPTION_IN_CONTACT_TABLE, DESCRIPTION_CY_IN_CONTACT_TABLE, "Ffacs " + DESCRIPTION_CY_IN_CONTACT_TABLE),
            Arguments.of(false, true, DESCRIPTION_IN_CONTACT_TABLE, DESCRIPTION_CY_IN_CONTACT_TABLE, DESCRIPTION_CY_IN_CONTACT_TABLE),
            Arguments.of(true, false, null, null, FAX),
            Arguments.of(true, true, null, null, FAX_CY)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForTestCreationWithoutContactType")
    void testCreationWithoutContactType(final boolean isFax, final boolean isWelsh, final String description, final String descriptionCy, final String expectedDescription) {
        if (isWelsh) {
            final Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        entity.setName(description);
        entity.setNameCy(descriptionCy);
        entity.setContactType(null);
        entity.setFax(isFax);

        final Contact contact = new Contact(entity);
        assertThat(contact.getName()).isEqualTo(expectedDescription);
        assertThat(contact.getExplanation()).isEqualTo(isWelsh ? entity.getExplanationCy() : entity.getExplanation());

        LocaleContextHolder.resetLocaleContext();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForTestCreationWithContactType() {
        return Stream.of(
            Arguments.of(false, false, DESCRIPTION_IN_ADMIN_TABLE),
            Arguments.of(true, false, DESCRIPTION_IN_ADMIN_TABLE + " fax"),
            Arguments.of(false, true, DESCRIPTION_CY_IN_ADMIN_TABLE),
            Arguments.of(true, true, "Ffacs " + DESCRIPTION_CY_IN_ADMIN_TABLE)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForTestCreationWithContactType")
    void testCreationWithContactType(final boolean isFax, final boolean isWelsh, final String expectedDescription) {
        if (isWelsh) {
            final Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        final ContactType contactType = new ContactType(1, DESCRIPTION_IN_ADMIN_TABLE, DESCRIPTION_CY_IN_ADMIN_TABLE);
        entity.setContactType(contactType);
        entity.setFax(isFax);

        Contact contact = new Contact(entity);
        assertThat(contact.getName()).isEqualTo(expectedDescription);
        assertThat(contact.getExplanation()).isEqualTo(isWelsh ? entity.getExplanationCy() : entity.getExplanation());

        LocaleContextHolder.resetLocaleContext();
    }
}
