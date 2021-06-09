package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourtLocalAuthority {

    private Integer areaOfLawId;
    private Integer courtId;
    private Integer localAuthorityId;

    public CourtLocalAuthority(uk.gov.hmcts.dts.fact.entity.CourtLocalAuthority courtLocalAuthority) {
        this.areaOfLawId = courtLocalAuthority.getAreaOfLaw().getId();
        this.courtId = courtLocalAuthority.getCourt().getId();
        this.localAuthorityId = courtLocalAuthority.getLocalAuthority().getId();
    }
}
