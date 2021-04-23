package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.Court;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourtGeneralInfo {
    private Boolean open;
    @JsonProperty("access_scheme")
    private Boolean accessScheme;
    private String info;
    @JsonProperty("info_cy")
    private String infoCy;
    private String alert;
    @JsonProperty("alert_cy")
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
