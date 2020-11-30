package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CourtGeneral {

    private String slug;
    private String name;
    private String nameCy;
    private String info;
    private String infoCy;
    private Boolean open;
    private Boolean inPerson;
    private Boolean accessScheme;
    @JsonProperty("urgent_message")
    private String alert;
    @JsonProperty("urgent_message_cy")
    private String alertCy;

    public CourtGeneral(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        this.slug = courtEntity.getSlug();
        this.name = courtEntity.getName();
        this.nameCy = courtEntity.getNameCy();
        this.info = courtEntity.getInfo();
        this.infoCy = courtEntity.getInfoCy();
        this.open = courtEntity.getDisplayed();
        this.accessScheme = courtEntity.getInPerson() == null ? null : courtEntity.getInPerson().getAccessScheme();
        this.inPerson = courtEntity.getInPerson() == null ? null : courtEntity.getInPerson().getIsInPerson();
        this.alert = courtEntity.getAlert();
        this.alertCy = courtEntity.getAlertCy();
    }
}
