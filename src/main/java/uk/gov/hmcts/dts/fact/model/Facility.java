package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.FacilityType;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
public class Facility {
    private String description;
    private String name;
    private Integer order;

    public Facility(uk.gov.hmcts.dts.fact.entity.Facility facility) {
        this.description = chooseString(facility.getDescriptionCy(), facility.getDescription());
        this.name = chooseString(facility.getFacilityType().getNameCy(), facility.getFacilityType().getName());
        this.order = ofNullable(facility.getFacilityType())
            .map(FacilityType::getOrder)
            .orElse(MAX_VALUE);
    }
}
