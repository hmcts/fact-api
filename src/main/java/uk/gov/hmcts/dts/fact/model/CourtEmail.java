package uk.gov.hmcts.dts.fact.model;

import lombok.Data;

@Data
public class CourtEmail {
    private String address;
    private String description;
    private String explanation;

    public CourtEmail(uk.gov.hmcts.dts.fact.entity.CourtEmail courtEmail) {
        this.address = courtEmail.getAddress();
        this.description = courtEmail.getDescription();
        this.explanation = courtEmail.getExplanation();
    }
}
