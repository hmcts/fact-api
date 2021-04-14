package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"description", "hours"})
public class OpeningTime {
    @JsonProperty("description")
    private String type;
    @JsonProperty(value = "description_cy")
    private String typeCy;
    private String hours;

    public OpeningTime(uk.gov.hmcts.dts.fact.entity.OpeningTime openingTime) {
        this.type = chooseString(openingTime.getTypeCy(), openingTime.getType());
        this.hours = openingTime.getHours();
    }
}
