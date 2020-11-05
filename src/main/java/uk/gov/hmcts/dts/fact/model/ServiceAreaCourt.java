package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ServiceAreaCourt {
    private String slug;
    private String catchmentType;

    public ServiceAreaCourt(uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt serviceAreaCourt) {
        this.slug = serviceAreaCourt.getCourt().getSlug();
        this.catchmentType = serviceAreaCourt.getCatchmentType();
    }
}
