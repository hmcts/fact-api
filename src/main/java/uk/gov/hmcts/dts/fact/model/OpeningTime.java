package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.OpeningType;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"description", "hours"})
public class OpeningTime {
    @JsonProperty("description")
    private String type;
    @JsonProperty("description_cy")
    private String typeCy;
    private String hours;

    public OpeningTime(uk.gov.hmcts.dts.fact.entity.OpeningTime openingTime) {
        final OpeningType openingType = openingTime.getOpeningType();
        final String openingTimeDescription = (openingType == null) ? openingTime.getType() : openingType.getName();
        final String openingTimeDescriptionCy = (openingType == null) ? openingTime.getTypeCy() : openingType.getNameCy();

        this.type = chooseString(openingTimeDescriptionCy, openingTimeDescription);
        this.hours = openingTime.getHours();
    }
}
