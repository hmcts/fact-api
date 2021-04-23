package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OpeningType {
    private Integer id;
    private String type;
    @JsonProperty("type_cy")
    private String typeCy;

    public OpeningType(final uk.gov.hmcts.dts.fact.entity.OpeningType openingType) {
        this.id = openingType.getId();
        this.type = openingType.getName();
        this.typeCy = openingType.getNameCy();
    }
}
