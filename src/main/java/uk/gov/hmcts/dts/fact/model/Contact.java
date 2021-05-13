package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.ContactType;

import java.util.Locale;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"number", "description", "explanation"})
public class Contact {
    private static final String FAX = "Fax";
    private static final String FAX_CY = "Ffacs";

    private String number;
    @JsonProperty("description")
    private String name;
    private String explanation;

    public Contact(uk.gov.hmcts.dts.fact.entity.Contact contact) {
        this.number = contact.getNumber();
        this.name = chooseString(getDescriptionCy(contact), getDescription(contact));
        this.explanation = chooseString(contact.getExplanationCy(), contact.getExplanation());
    }

    private String getDescription(final uk.gov.hmcts.dts.fact.entity.Contact contact) {
        final ContactType contactType = contact.getContactType();
        String contactDescription = (contactType == null) ? contact.getName() : contactType.getName();

        if (contact.isFax() && !contactDescription.equalsIgnoreCase(FAX)) {
            return new StringBuilder(contactDescription)
                .append(" ")
                .append(FAX.toLowerCase(Locale.getDefault()))
                .toString();
        }
        return contactDescription;
    }

    private String getDescriptionCy(final uk.gov.hmcts.dts.fact.entity.Contact contact) {
        final ContactType contactType = contact.getContactType();
        String contactDescription = (contactType == null) ? contact.getNameCy() : contactType.getNameCy();

        if (contact.isFax() && !contactDescription.equalsIgnoreCase(FAX_CY)) {
            return new StringBuilder(FAX_CY)
                .append(" ")
                .append(contactDescription)
                .toString();
        }
        return contactDescription;
    }
}
