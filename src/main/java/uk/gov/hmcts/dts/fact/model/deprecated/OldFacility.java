package uk.gov.hmcts.dts.fact.model.deprecated;

import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
public class OldFacility {
    
    private String description;
    private String name;

    public OldFacility(uk.gov.hmcts.dts.fact.entity.Facility facility) {
        this.description = chooseString(facility.getDescriptionCy(), facility.getDescription());
        this.name = facility.getName();
    }
}