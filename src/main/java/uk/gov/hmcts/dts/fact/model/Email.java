package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
public class Email {
    private String address;
    private String description;
    private String explanation;

    public Email(uk.gov.hmcts.dts.fact.entity.Email email, boolean welsh) {
        this.address = email.getAddress();
        this.description = email.getDescription();
        this.explanation = chooseString(welsh, email.getExplanationCy(), email.getExplanation());
    }
}
