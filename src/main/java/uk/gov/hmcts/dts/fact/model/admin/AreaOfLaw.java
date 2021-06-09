package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AreaOfLaw {

    private Integer id;
    private String name;

    public AreaOfLaw(uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw) {
        this.id = areaOfLaw.getId();
        this.name = areaOfLaw.getName();
    }
}
