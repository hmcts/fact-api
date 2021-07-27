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
public class AddressType {
    private Integer id;
    private String name;
    @JsonProperty("name_cy")
    private String nameCy;

    public AddressType(uk.gov.hmcts.dts.fact.entity.AddressType addressType) {
        this.id = addressType.getId();
        this.name = addressType.getName();
        this.nameCy = addressType.getNameCy();
    }
}
