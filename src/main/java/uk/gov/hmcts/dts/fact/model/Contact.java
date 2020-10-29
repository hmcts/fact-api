package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"number", "description", "explanation"})
public class Contact {
    private String number;
    @JsonProperty("description")
    private String name;
    private String explanation;

    public Contact(uk.gov.hmcts.dts.fact.entity.Contact contact) {
        this.number = contact.getNumber();
        this.name = contact.getName();
        this.explanation = chooseString(contact.getExplanationCy(), contact.getExplanation());
    }
}
