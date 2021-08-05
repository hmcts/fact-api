package uk.gov.hmcts.dts.fact.model.admin;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class FacilityType {
    private Integer id;
    private String name;
    private String nameCy;
    private String image;
    private String imageDescription;
    private String imageFilePath;
    private Integer order;

    public FacilityType(uk.gov.hmcts.dts.fact.entity.FacilityType facilityType) {
        this.id = facilityType.getId();
        this.name = facilityType.getName();
        this.nameCy = facilityType.getNameCy();
        this.image = facilityType.getImage();
        this.imageDescription = facilityType.getImageDescription();
        this.imageFilePath = facilityType.getImageFilePath();
        this.order = facilityType.getOrder();
    }

}
