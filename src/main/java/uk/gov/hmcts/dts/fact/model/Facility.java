package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
public class Facility {
    private String description;
    private String name;

    public Facility(uk.gov.hmcts.dts.fact.entity.Facility facility, boolean welsh) {
        this.description = chooseString(welsh, facility.getDescriptionCy(), facility.getDescription());
        this.name = facility.getName();
    }
}
