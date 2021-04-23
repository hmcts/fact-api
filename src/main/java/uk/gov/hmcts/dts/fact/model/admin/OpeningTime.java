package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpeningTime {
    @JsonProperty("type_id")
    private Integer typeId;
    private String hours;

    public OpeningTime(uk.gov.hmcts.dts.fact.entity.OpeningTime openingTime) {
        this.typeId = openingTime.getOpeningTypeId();
        this.hours = openingTime.getHours();
    }
}
