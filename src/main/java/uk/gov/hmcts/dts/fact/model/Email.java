package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Email {
    private String address;
    private String description;
    private String explanation;

    public Email(uk.gov.hmcts.dts.fact.entity.Email email, boolean welsh) {
        this.address = email.getAddress();
        this.description = email.getDescription();
        this.explanation = welsh ? email.getExplanationCy() : email.getExplanation();
    }
}
