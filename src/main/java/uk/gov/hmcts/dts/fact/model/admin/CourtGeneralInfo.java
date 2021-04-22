package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.Court;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourtGeneralInfo {
    private Boolean open;
    private Boolean accessScheme;
    private String info;
    private String infoCy;
    private String alert;
    private String alertCy;

    public CourtGeneralInfo(Court court) {
        this.open = court.getDisplayed();
        this.accessScheme = court.getInPerson() == null ? null : court.getInPerson().getAccessScheme();
        this.info = court.getInfo();
        this.infoCy = court.getInfoCy();
        this.alert = court.getAlert();
        this.alertCy = court.getAlertCy();
    }
}
