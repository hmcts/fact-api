package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.Court;


@Data
@NoArgsConstructor
public class ServiceAreaCourt {
    private String slug;
    private String catchmentType;
    private String courtName;
    private String courtNameCy;

    public ServiceAreaCourt(uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt serviceAreaCourt) {
        this.catchmentType = serviceAreaCourt.getCatchmentType();
        final Court court = serviceAreaCourt.getCourt();
        this.courtName = court.getName();
        this.courtNameCy = court.getNameCy();
        this.slug = court.getSlug();
    }
}
