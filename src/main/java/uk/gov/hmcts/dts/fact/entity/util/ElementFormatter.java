package uk.gov.hmcts.dts.fact.entity.util;

import org.apache.commons.lang.StringUtils;
import uk.gov.hmcts.dts.fact.entity.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.DxCode;
import uk.gov.hmcts.dts.fact.entity.Email;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;

import static java.lang.String.format;

/**
 * Utility class to format entities into strings.
 */
@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
public final class ElementFormatter {

    private static String explanation = ", Explanation: %s";

    private ElementFormatter() {
    }

    /**
     * Formats a contact entity into a string.
     *
     * @param contact the contact entity to format
     * @return the formatted string
     */
    public static String formatContact(final Contact contact) {
        final StringBuilder formatted = new StringBuilder(format(
            "Number: %s, Description: %s",
            contact.getNumber(),
            contact.getDescription(contact)
        ));

        if (StringUtils.isNotBlank(contact.getExplanation())) {
            formatted.append(format(explanation, contact.getExplanation()));
        }
        return formatted.toString();
    }

    /**
     * Formats a dx code entity into a string.
     *
     * @param dx the dx code entity to format
     * @return the formatted string
     */
    public static String formatDxCode(final DxCode dx) {
        final StringBuilder formatted = new StringBuilder(format("Code: %s", dx.getCode()));
        if (StringUtils.isNotBlank(dx.getExplanation())) {
            formatted.append(format(explanation, dx.getExplanation()));
        }
        return formatted.toString();
    }

    /**
     * Formats an email entity into a string.
     *
     * @param email the email entity to format
     * @return the formatted string
     */
    public static String formatEmail(final Email email) {
        final StringBuilder formatted = new StringBuilder(format(
            "Address: %s, Description: %s",
            email.getAddress(),
            email.getDescription(email)
        ));
        if (StringUtils.isNotBlank(email.getExplanation())) {
            formatted.append(format(explanation, email.getExplanation()));
        }
        return formatted.toString();
    }

    /**
     * Formats an opening time entity into a string.
     *
     * @param openingTime the opening time entity to format
     * @return the formatted string
     */
    public static String formatOpeningTime(OpeningTime openingTime) {
        return format("Description: %s, Hours: %s", openingTime.getDescription(openingTime), openingTime.getHours());
    }

    /**
     * Formats an application update entity into a string.
     *
     * @param applicationUpdate the application update entity to format
     * @return the formatted string
     */
    public static String formatApplicationUpdate(final ApplicationUpdate applicationUpdate) {
        final StringBuilder formatted = new StringBuilder(format("Type: %s, ", applicationUpdate.getType()));
        if (StringUtils.isNotBlank(applicationUpdate.getEmail())) {
            return formatted.append(format("Email: %s", applicationUpdate.getEmail()))
                .toString();
        }
        return formatted
            .append(format("External link: %s, External link description: %s",
                           applicationUpdate.getExternalLink(),
                           applicationUpdate.getExternalLinkDescription()))
            .toString();
    }
}
