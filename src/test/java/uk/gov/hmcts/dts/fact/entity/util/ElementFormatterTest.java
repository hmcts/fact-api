package uk.gov.hmcts.dts.fact.entity.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.dts.fact.entity.*;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ElementFormatterTest {
    private static final String DX = "DX";

    private static final String TEST_CONTACT_NUMBER = "123";
    private static final String TEST_CONTACT_DESCRIPTION = "Contact description";
    private static final String TEST_CONTACT_DESCRIPTION_CY = "Contact description cy";
    private static final String TEST_CONTACT_EXPLANATION = "Contact explanation";
    private static final String TEST_CONTACT_EXPLANATION_CY = "Contact explanation cy";
    private static final String TEST_CONTACT_ADMIN_DESCRIPTION = "Contact admin description";
    private static final String TEST_CONTACT_ADMIN_DESCRIPTION_CY = "Contact admin description cy";
    private static final ContactType TEST_CONTACT_ADMIN_TYPE = new ContactType(1, TEST_CONTACT_ADMIN_DESCRIPTION, TEST_CONTACT_ADMIN_DESCRIPTION_CY);
    private static final ContactType TEST_DX_ADMIN_TYPE = new ContactType(1, DX, DX);

    private static final String TEST_EMAIL_ADDRESS = "test@test.com";
    private static final String TEST_EMAIL_DESCRIPTION = "Email description";
    private static final String TEST_EMAIL_DESCRIPTION_CY = "Email description cy";
    private static final String TEST_EMAIL_EXPLANATION = "Email explanation";
    private static final String TEST_EMAIL_EXPLANATION_CY = "Email explanation cy";
    private static final String TEST_EMAIL_ADMIN_DESCRIPTION = "Email admin description";
    private static final String TEST_EMAIL_ADMIN_DESCRIPTION_CY = "Email admin description cy";
    private static final EmailType TEST_EMAIL_ADMIN_TYPE = new EmailType(1, TEST_EMAIL_ADMIN_DESCRIPTION, TEST_EMAIL_ADMIN_DESCRIPTION_CY);

    private static final String TEST_OPENING_HOURS = "9 to 5";
    private static final String TEST_OPENING_DESCRIPTION = "Opening description";
    private static final String TEST_OPENING_DESCRIPTION_CY = "Opening description cy";
    private static final String TEST_OPENING_ADMIN_DESCRIPTION = "Opening admin description";
    private static final String TEST_OPENING_ADMIN_DESCRIPTION_CY = "Opening admin description cy";
    private static final OpeningType TEST_OPENING_ADMIN_TYPE = new OpeningType(1, TEST_OPENING_ADMIN_DESCRIPTION, TEST_OPENING_ADMIN_DESCRIPTION_CY);

    private static final String TEST_APPLICATION_UPDATE_TYPE = "Application update type";
    private static final String TEST_APPLICATION_UPDATE_TYPE_CY = "Application update type cy";
    private static final String TEST_EXTERNAL_LINK = "http://test.com/";
    private static final String TEST_EXTERNAL_LINK_DESCRIPTION = "External link description";
    private static final String TEST_EXTERNAL_LINK_DESCRIPTION_CY = "External link description cy";

    private static final String DESCRIPTION_PREFIX = "Description: ";
    private static final String EXPLANATION_PREFIX = "Explanation: ";
    private static final String NUMBER_PREFIX = "Number: ";
    private static final String ADDRESS_PREFIX = "Address: ";
    private static final String HOURS_PREFIX = "Hours: ";
    private static final String TYPE_PREFIX = "Type: ";
    private static final String EMAIL_PREFIX = "Email: ";
    private static final String EXTERNAL_LINK_PREFIX = "External link: ";
    private static final String EXTERNAL_LINK_DESCRIPTION_PREFIX = "External link description: ";

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForFormatContact() {
        return Stream.of(
            Arguments.of(TEST_CONTACT_ADMIN_TYPE,
                         TEST_CONTACT_EXPLANATION,
                         NUMBER_PREFIX + TEST_CONTACT_NUMBER + ", " + DESCRIPTION_PREFIX + TEST_CONTACT_ADMIN_DESCRIPTION + ", " + EXPLANATION_PREFIX + TEST_CONTACT_EXPLANATION),
            Arguments.of(TEST_CONTACT_ADMIN_TYPE,
                         null,
                         NUMBER_PREFIX + TEST_CONTACT_NUMBER + ", " + DESCRIPTION_PREFIX + TEST_CONTACT_ADMIN_DESCRIPTION),
            Arguments.of(TEST_CONTACT_ADMIN_TYPE,
                         " ",
                         NUMBER_PREFIX + TEST_CONTACT_NUMBER + ", " + DESCRIPTION_PREFIX + TEST_CONTACT_ADMIN_DESCRIPTION),
            Arguments.of(TEST_DX_ADMIN_TYPE,
                         "",
                         NUMBER_PREFIX + TEST_CONTACT_NUMBER)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForFormatContact")
    void testFormatContact(final ContactType contactType, final String explanation, final String expectedResult) {
        final Contact contact = new Contact(contactType, TEST_CONTACT_NUMBER, explanation, TEST_CONTACT_EXPLANATION_CY);
        contact.setDescription(TEST_CONTACT_DESCRIPTION);
        contact.setDescriptionCy(TEST_CONTACT_DESCRIPTION_CY);

        assertThat(ElementFormatter.formatContact(contact)).isEqualTo(expectedResult);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForFormatEmail() {
        return Stream.of(
            Arguments.of(TEST_EMAIL_EXPLANATION,
                         ADDRESS_PREFIX + TEST_EMAIL_ADDRESS + ", " + DESCRIPTION_PREFIX + TEST_EMAIL_ADMIN_DESCRIPTION + ", " + EXPLANATION_PREFIX + TEST_EMAIL_EXPLANATION),
            Arguments.of(null,
                         ADDRESS_PREFIX + TEST_EMAIL_ADDRESS + ", " + DESCRIPTION_PREFIX + TEST_EMAIL_ADMIN_DESCRIPTION),
            Arguments.of(" ",
                         ADDRESS_PREFIX + TEST_EMAIL_ADDRESS + ", " + DESCRIPTION_PREFIX + TEST_EMAIL_ADMIN_DESCRIPTION)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForFormatEmail")
    void testFormatEmail(final String explanation, final String expectedResult) {
        final Email email = new Email(TEST_EMAIL_ADDRESS, explanation, TEST_EMAIL_EXPLANATION_CY, TEST_EMAIL_ADMIN_TYPE);
        email.setDescription(TEST_EMAIL_DESCRIPTION);
        email.setDescriptionCy(TEST_EMAIL_DESCRIPTION_CY);

        assertThat(ElementFormatter.formatEmail(email)).isEqualTo(expectedResult);
    }

    @Test
    void testFormatOpeningTime() {
        final OpeningTime openingTime = new OpeningTime(TEST_OPENING_ADMIN_TYPE, TEST_OPENING_HOURS);
        openingTime.setDescription(TEST_OPENING_DESCRIPTION);
        openingTime.setDescriptionCy(TEST_OPENING_DESCRIPTION_CY);

        final String expectedResult = DESCRIPTION_PREFIX + TEST_OPENING_ADMIN_DESCRIPTION + ", " + HOURS_PREFIX + TEST_OPENING_HOURS;
        assertThat(ElementFormatter.formatOpeningTime(openingTime)).isEqualTo(expectedResult);
    }

    @Test
    void testFormatApplicationUpdatesWithEmail() {
        final ApplicationUpdate applicationUpdate = new ApplicationUpdate();
        applicationUpdate.setType(TEST_APPLICATION_UPDATE_TYPE);
        applicationUpdate.setTypeCy(TEST_APPLICATION_UPDATE_TYPE_CY);
        applicationUpdate.setEmail(TEST_EMAIL_ADDRESS);

        final String expectedResult = TYPE_PREFIX + TEST_APPLICATION_UPDATE_TYPE + ", "
            + EMAIL_PREFIX + TEST_EMAIL_ADDRESS;
        assertThat(ElementFormatter.formatApplicationUpdate(applicationUpdate)).isEqualTo(expectedResult);
    }

    @Test
    void testFormatApplicationUpdatesWithExternalLink() {
        final ApplicationUpdate applicationUpdate = new ApplicationUpdate();
        applicationUpdate.setType(TEST_APPLICATION_UPDATE_TYPE);
        applicationUpdate.setTypeCy(TEST_APPLICATION_UPDATE_TYPE_CY);
        applicationUpdate.setExternalLink(TEST_EXTERNAL_LINK);
        applicationUpdate.setExternalLinkDescription(TEST_EXTERNAL_LINK_DESCRIPTION);
        applicationUpdate.setExternalLinkDescriptionCy(TEST_EXTERNAL_LINK_DESCRIPTION_CY);

        final String expectedResult = TYPE_PREFIX + TEST_APPLICATION_UPDATE_TYPE + ", "
            + EXTERNAL_LINK_PREFIX + TEST_EXTERNAL_LINK + ", "
            + EXTERNAL_LINK_DESCRIPTION_PREFIX + TEST_EXTERNAL_LINK_DESCRIPTION;
        assertThat(ElementFormatter.formatApplicationUpdate(applicationUpdate)).isEqualTo(expectedResult);
    }
}
