package uk.gov.hmcts.dts.fact.entity.util;

import org.apache.commons.lang.StringUtils;
import uk.gov.hmcts.dts.fact.entity.*;

import static java.lang.String.format;

public final class ElementFormatter {

    private ElementFormatter() {
    }

    public static String formatContact(final Contact contact) {
        final StringBuffer formatted = new StringBuffer(format(
            "Number: %s, Description: %s",
            contact.getNumber(),
            contact.getDescription(contact)
        ));

        if (StringUtils.isNotBlank(contact.getExplanation())) {
            formatted.append(format(", Explanation: %s", contact.getExplanation()));
        }
        return formatted.toString();
    }

    public static String formatDxCode(final DxCode dx) {
        final StringBuffer formatted = new StringBuffer(format("Code: %s", dx.getCode()));
        if (StringUtils.isNotBlank(dx.getExplanation())) {
            formatted.append(format(", Explanation: %s", dx.getExplanation()));
        }
        return formatted.toString();
    }

    public static String formatEmail(final Email email) {
        final StringBuffer formatted = new StringBuffer(format(
            "Address: %s, Description: %s",
            email.getAddress(),
            email.getDescription(email)
        ));
        if (StringUtils.isNotBlank(email.getExplanation())) {
            formatted.append(format(", Explanation: %s", email.getExplanation()));
        }
        return formatted.toString();
    }

    public static String formatOpeningTime(OpeningTime openingTime) {
        return format("Description: %s, Hours: %s", openingTime.getDescription(openingTime), openingTime.getHours());
    }

    public static String formatApplicationUpdate(final ApplicationUpdate applicationUpdate) {
        final StringBuffer formatted = new StringBuffer(format("Type: %s, ", applicationUpdate.getType()));
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
