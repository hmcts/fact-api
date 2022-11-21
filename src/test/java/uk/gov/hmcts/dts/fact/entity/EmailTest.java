package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailTest {
    private static final String EMAIL_ADDRESS = "test@test.com";
    private static final String EXPLANATION = "explanation";
    private static final String EXPLANATION_CY = "explanation cy";

    private static final String DESCRIPTION = "description";
    private static final String DESCRIPTION_CY = "description cy";
    private static final String ADMIN_DESCRIPTION = "admin description";
    private static final String ADMIN_DESCRIPTION_CY = "admin description cy";

    @Test
    void testEmailTypeConstructor() {
        EmailType emailType = new EmailType(1, "desc", "desc cy");
        Email email = new Email(
            "address", "expl", "expl cy", emailType);
        assertEquals(email.getAdminType(), emailType);
        assertEquals("expl",email.getExplanation());
        assertEquals("expl cy",email.getExplanationCy());
        assertEquals(email.getAddress(), "address");
        assertEquals( "",email.getDescription());
        assertNull(email.getDescriptionCy());
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForGetDescription() {
        return Stream.of(
            Arguments.of(DESCRIPTION, ADMIN_DESCRIPTION, ADMIN_DESCRIPTION),
            Arguments.of(null, ADMIN_DESCRIPTION, ADMIN_DESCRIPTION),
            Arguments.of(DESCRIPTION, null, DESCRIPTION)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetDescription")
    void testGetDescription(final String description, final String adminDescription, final String expectedDescription) {
        final EmailType emailType = (adminDescription == null)
            ? null
            : new EmailType(1, adminDescription, ADMIN_DESCRIPTION_CY);

        final Email email = new Email(EMAIL_ADDRESS, EXPLANATION, EXPLANATION_CY, emailType);
        email.setDescription(description);
        email.setDescriptionCy(DESCRIPTION_CY);

        assertThat(email.getDescription(email)).isEqualTo(expectedDescription);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForGetDescriptionCy() {
        return Stream.of(
            Arguments.of(DESCRIPTION_CY, ADMIN_DESCRIPTION_CY, ADMIN_DESCRIPTION_CY),
            Arguments.of(null, ADMIN_DESCRIPTION_CY, ADMIN_DESCRIPTION_CY),
            Arguments.of(DESCRIPTION_CY, null, DESCRIPTION_CY)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetDescriptionCy")
    void testGetDescriptionCy(final String descriptionCy, final String adminDescriptionCy, final String expectedDescription) {
        final EmailType emailType = (adminDescriptionCy == null)
            ? null
            : new EmailType(1, ADMIN_DESCRIPTION, adminDescriptionCy);

        final Email email = new Email(EMAIL_ADDRESS, EXPLANATION, EXPLANATION_CY, emailType);
        email.setDescription(DESCRIPTION);
        email.setDescriptionCy(descriptionCy);

        assertThat(email.getDescriptionCy(email)).isEqualTo(expectedDescription);
    }
}
