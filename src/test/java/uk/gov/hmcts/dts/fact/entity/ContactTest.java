package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactTest {
    private static final String TYPE = "type";
    private static final String TYPE_CY = "type cy";
    private static final String CONTACT_NUMBER = "123";
    private static final String EXPLANATION = "explanation";
    private static final String EXPLANATION_CY = "explanation cy";

    private static final String DESCRIPTION = "description";
    private static final String DESCRIPTION_CY = "description cy";
    private static final String ADMIN_DESCRIPTION = "admin description";
    private static final String ADMIN_DESCRIPTION_CY = "admin description cy";

    private static final String FAX = "Fax";
    private static final String FAX_SUFFIX = " fax";
    private static final String FAX_CY = "Ffacs";

    @Test
    void testCreation() {
        ContactType contactType = new ContactType(1, TYPE, TYPE_CY);
        Contact contact = new Contact(contactType, CONTACT_NUMBER, EXPLANATION, EXPLANATION_CY, true);

        assertThat(contact.getAdminType()).isEqualTo(contactType);
        assertThat(contact.getNumber()).isEqualTo(CONTACT_NUMBER);
        assertThat(contact.getExplanation()).isEqualTo(EXPLANATION);
        assertThat(contact.getExplanationCy()).isEqualTo(EXPLANATION_CY);
        assertThat(contact.isFax()).isEqualTo(true);
    }

    @Test
    void testCreationWithoutFax() {
        ContactType contactType = new ContactType(1, TYPE, TYPE_CY);
        Contact contact = new Contact(contactType, CONTACT_NUMBER, EXPLANATION, EXPLANATION_CY);

        assertThat(contact.getAdminType()).isEqualTo(contactType);
        assertThat(contact.getNumber()).isEqualTo(CONTACT_NUMBER);
        assertThat(contact.getExplanation()).isEqualTo(EXPLANATION);
        assertThat(contact.getExplanationCy()).isEqualTo(EXPLANATION_CY);
        assertThat(contact.isFax()).isEqualTo(false);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForGetDescription() {
        return Stream.of(
            Arguments.of(DESCRIPTION, ADMIN_DESCRIPTION, false, ADMIN_DESCRIPTION),
            Arguments.of(DESCRIPTION, ADMIN_DESCRIPTION, true, ADMIN_DESCRIPTION + FAX_SUFFIX),
            Arguments.of(null, ADMIN_DESCRIPTION, false, ADMIN_DESCRIPTION),
            Arguments.of(null, ADMIN_DESCRIPTION, true, ADMIN_DESCRIPTION + FAX_SUFFIX),
            Arguments.of(DESCRIPTION, null, false, DESCRIPTION),
            Arguments.of(null, null, true, FAX)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetDescription")
    void testGetDescription(final String description, final String adminDescription, final boolean isFax, final String expectedDescription) {
        final ContactType contactType = (adminDescription == null)
            ? null
            : new ContactType(1, adminDescription, ADMIN_DESCRIPTION_CY);

        final Contact contact = new Contact(contactType, CONTACT_NUMBER, EXPLANATION, EXPLANATION_CY, isFax);
        contact.setDescription(description);
        contact.setDescriptionCy(DESCRIPTION_CY);

        assertThat(contact.getDescription(contact)).isEqualTo(expectedDescription);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForGetDescriptionCy() {
        return Stream.of(
            Arguments.of(DESCRIPTION_CY, ADMIN_DESCRIPTION_CY, false, ADMIN_DESCRIPTION_CY),
            Arguments.of(DESCRIPTION_CY, ADMIN_DESCRIPTION_CY, true, FAX_CY + " " + ADMIN_DESCRIPTION_CY),
            Arguments.of(null, ADMIN_DESCRIPTION_CY, false, ADMIN_DESCRIPTION_CY),
            Arguments.of(null, ADMIN_DESCRIPTION_CY, true, FAX_CY + " " + ADMIN_DESCRIPTION_CY),
            Arguments.of(DESCRIPTION_CY, null, false, DESCRIPTION_CY),
            Arguments.of(null, null, true, FAX_CY)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetDescriptionCy")
    void testGetDescriptionCy(final String descriptionCy, final String adminDescriptionCy, final boolean isFax, final String expectedDescription) {
        final ContactType contactType = (adminDescriptionCy == null)
            ? null
            : new ContactType(1, ADMIN_DESCRIPTION, adminDescriptionCy);

        final Contact contact = new Contact(contactType, CONTACT_NUMBER, EXPLANATION, EXPLANATION_CY, isFax);
        contact.setDescription(DESCRIPTION);
        contact.setDescriptionCy(descriptionCy);

        assertThat(contact.getDescriptionCy(contact)).isEqualTo(expectedDescription);
    }
}
