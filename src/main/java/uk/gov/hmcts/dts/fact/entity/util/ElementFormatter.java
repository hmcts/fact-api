package uk.gov.hmcts.dts.fact.entity.util;

import org.apache.commons.lang.StringUtils;
import uk.gov.hmcts.dts.fact.entity.*;

import static java.lang.String.format;

public final class ElementFormatter {
    private static final String DX = "DX";

    private ElementFormatter() {
    }

    public static String formatContact(final Contact contact) {
        final StringBuffer formatted = new StringBuffer(format("Number: %s", contact.getNumber()));
        final String description = contact.getDescription(contact);
        if (!DX.equalsIgnoreCase(description)) {
            formatted.append(format(", Description: %s", description));
        }
        if (StringUtils.isNotBlank(contact.getExplanation())) {
            formatted.append(format(", Explanation: %s", contact.getExplanation()));
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
}
