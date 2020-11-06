package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.Court;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;


@Data
@NoArgsConstructor
public class ServiceAreaCourt {
    private String slug;
    private String catchmentType;
    private String courtName;

    public ServiceAreaCourt(uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt serviceAreaCourt) {
        this.catchmentType = serviceAreaCourt.getCatchmentType();
        final Court court = serviceAreaCourt.getCourt();
        this.courtName = chooseString(court.getNameCy(), court.getName());
        this.slug = court.getSlug();
    }
}
