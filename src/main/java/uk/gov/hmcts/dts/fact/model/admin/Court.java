package uk.gov.hmcts.dts.fact.model.admin;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.model.OpeningTime;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Court {

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
    private List<OpeningTime> openingTimes;
    @JsonProperty("types")
    private List<CourtType> courtTypes;

    private Boolean commonPlatform;

    public Court(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
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
        this.openingTimes = ofNullable(courtEntity.getCourtOpeningTimes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(cot -> cot.getOpeningTime())
            .map(OpeningTime::new).collect(toList());
        this.courtTypes = ofNullable(courtEntity.getCourtTypes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtType::new).collect(toList());
        this.commonPlatform = courtEntity.getInPerson() == null ? null : courtEntity.getInPerson().getCommonPlatform();
    }
}
