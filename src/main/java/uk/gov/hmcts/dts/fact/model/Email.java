package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.EmailType;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
public class Email {
    private String address;
    private String description;
    private String explanation;

    public Email(uk.gov.hmcts.dts.fact.entity.Email email) {
        final EmailType emailType = email.getAdminEmailType();
        this.address = email.getAddress();
        description = (emailType == null)
            ? chooseString(email.getDescriptionCy(), email.getDescription())
            : chooseString(emailType.getDescriptionCy(), emailType.getDescription());
        this.explanation = chooseString(email.getExplanationCy(), email.getExplanation());
    }
}
