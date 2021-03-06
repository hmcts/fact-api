package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Email {
    private String address;
    private String explanation;
    private String explanationCy;
    private int adminEmailTypeId;

    public Email(uk.gov.hmcts.dts.fact.entity.Email email) {
        this.address = email.getAddress();
        this.explanation = email.getExplanation();
        this.explanationCy = email.getExplanationCy();

        if (email.getAdminType() != null) {
            this.adminEmailTypeId = email.getAdminType().getId();
        }
    }
}
