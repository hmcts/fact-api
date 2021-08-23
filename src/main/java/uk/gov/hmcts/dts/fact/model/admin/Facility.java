package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facility {
    private String name;
    private String description;
    private String descriptionCy;

    public Facility(uk.gov.hmcts.dts.fact.entity.Facility facility) {
        this.name = facility.getName();
        this.description = facility.getDescription();
        this.descriptionCy = facility.getDescriptionCy();
    }
}
