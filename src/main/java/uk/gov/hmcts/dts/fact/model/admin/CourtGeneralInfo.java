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
    private String name;
    private Boolean open;
    @JsonProperty("in_person")
    private Boolean inPerson;
    @JsonProperty("access_scheme")
    private Boolean accessScheme;
    private String info;
    @JsonProperty("info_cy")
    private String infoCy;
    private String alert;
    @JsonProperty("alert_cy")
    private String alertCy;
    @JsonProperty("sc_intro_paragraph")
    private String scIntroParagraph;
    @JsonProperty("sc_intro_paragraph_cy")
    private String scIntroParagraphCy;
    private boolean serviceCentre;

    public CourtGeneralInfo(Court court) {
        this.name = court.getName();
        this.open = court.getDisplayed();
        this.inPerson = court.getInPerson() != null && court.getInPerson().getIsInPerson();
        this.accessScheme = court.getInPerson() != null && court.getInPerson().getAccessScheme();
        this.info = court.getInfo();
        this.infoCy = court.getInfoCy();
        this.alert = court.getAlert();
        this.alertCy = court.getAlertCy();
        this.serviceCentre = court.getServiceAreas().size() > 0;
        this.scIntroParagraph = court.getServiceCentre() == null
            ? "" : court.getServiceCentre().getIntroParagraph();
        this.scIntroParagraphCy = court.getServiceCentre() == null
            ? "" : court.getServiceCentre().getIntroParagraphCy();
    }
}
