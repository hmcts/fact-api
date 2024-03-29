package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facility {
    private Integer id;
    private String description;
    private String descriptionCy;

    public Facility(uk.gov.hmcts.dts.fact.entity.Facility facility) {
        this.id = facility.getFacilityType().getId();
        this.description = facility.getDescription();
        this.descriptionCy = facility.getDescriptionCy();
    }
}
