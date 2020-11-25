package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonPropertyOrder({"name", "name_cy", "info", "info_cy", "open", "urgent_message", "urgent_message_cy"})
@NoArgsConstructor
@Getter
@Setter
public class CourtGeneral extends CourtInfo {

    private String slug;
    private String name;
    @JsonProperty("name_cy")
    private String nameCy;
    private Boolean open;
    @JsonProperty("urgent_message")
    private String alert;
    @JsonProperty("urgent_message_cy")
    private String alertCy;

    public CourtGeneral(String slug, String name, String nameCy, String info, String infoCy, Boolean open, String alert, String alertCy) {
        super(info, infoCy);
        this.slug = slug;
        this.name = name;
        this.nameCy = nameCy;
        this.open = open;
        this.alert = alert;
        this.alertCy = alertCy;
    }

    public CourtGeneral(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        super(courtEntity);
        this.slug = courtEntity.getSlug();
        this.name = courtEntity.getName();
        this.nameCy = courtEntity.getNameCy();
        this.open = courtEntity.getDisplayed();
        this.alert = courtEntity.getAlert();
        this.alertCy = courtEntity.getAlertCy();
    }
}
