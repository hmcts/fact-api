package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Facility {
    private String description;
    private String name;

    public Facility(uk.gov.hmcts.dts.fact.entity.Facility facility, boolean welsh) {
        this.description = welsh ? facility.getDescriptionCy() : facility.getDescription();
        this.name = facility.getName();
    }
}
