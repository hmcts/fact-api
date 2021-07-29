package uk.gov.hmcts.dts.fact.model.admin;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class FacilityType {
    private Integer id;
    private String name;
    private Integer order;

    public FacilityType(uk.gov.hmcts.dts.fact.entity.FacilityType facilityType) {
        this.id = facilityType.getId();
        this.name = facilityType.getName();
        this.order = facilityType.getOrder();
    }

}
