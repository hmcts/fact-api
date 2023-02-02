package uk.gov.hmcts.dts.fact.entity.util;

import org.apache.commons.lang.StringUtils;
import uk.gov.hmcts.dts.fact.entity.*;

import static java.lang.String.format;

@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
public final class ElementFormatter {

    private static String explanation = ", Explanation: %s";

    private ElementFormatter() {
    }

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

    public static String formatDxCode(final DxCode dx) {
        final StringBuilder formatted = new StringBuilder(format("Code: %s", dx.getCode()));
        if (StringUtils.isNotBlank(dx.getExplanation())) {
            formatted.append(format(explanation, dx.getExplanation()));
        }
        return formatted.toString();
    }

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

    public static String formatOpeningTime(OpeningTime openingTime) {
        return format("Description: %s, Hours: %s", openingTime.getDescription(openingTime), openingTime.getHours());
    }

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
