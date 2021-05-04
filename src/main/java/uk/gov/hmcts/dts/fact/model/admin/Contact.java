package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    @JsonProperty("type_id")
    private Integer typeId;
    private String number;
    private String explanation;
    @JsonProperty("explanation_cy")
    private String explanationCy;

    public Contact(uk.gov.hmcts.dts.fact.entity.Contact contact) {
        if (contact.getContactType() != null) {
            this.typeId = contact.getContactType().getId();
        }
        this.number = contact.getNumber();
        this.explanation = contact.getExplanation();
        this.explanationCy = contact.getExplanationCy();
    }
}
