package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        this.explanation = contact.getExplanation();
    }
}
