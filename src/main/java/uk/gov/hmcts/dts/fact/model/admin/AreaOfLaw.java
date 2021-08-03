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
    private boolean singlePointEntry;

    // For returning the complete list of areas of law, not specific to a court
    public AreaOfLaw(uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw) {
        this.id = areaOfLaw.getId();
        this.name = areaOfLaw.getName();
    }

    public AreaOfLaw(uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw, boolean singlePointEntry) {
        this.id = areaOfLaw.getId();
        this.name = areaOfLaw.getName();
        this.singlePointEntry = singlePointEntry;
    }
}
