package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AreaOfLaw {

    private Integer id;
    private String name;
    private boolean singlePointEntry;
    @JsonProperty("external_link")
    private String externalLink;
    @JsonProperty("external_link_desc")
    private String externalLinkDescription;
    @JsonProperty("external_link_desc_cy")
    private String externalLinkDescriptionCy;
    @JsonProperty("alt_name")
    private String alternativeName;
    @JsonProperty("alt_name_cy")
    private String alternativeNameCy;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("display_name_cy")
    private String displayNameCy;
    @JsonProperty("display_external_link")
    private String displayExternalLink;

    // For returning the complete list of areas of law, not specific to a court
    public AreaOfLaw(uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw) {
        this(areaOfLaw, false);
    }

    public AreaOfLaw(uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw, boolean singlePointEntry) {
        this.id = areaOfLaw.getId();
        this.name = areaOfLaw.getName();
        this.singlePointEntry = singlePointEntry;
        this.externalLink = areaOfLaw.getExternalLink();
        this.externalLinkDescription = areaOfLaw.getExternalLinkDescription();
        this.externalLinkDescriptionCy = areaOfLaw.getExternalLinkDescriptionCy();
        this.alternativeName = areaOfLaw.getAltName();
        this.alternativeNameCy = areaOfLaw.getAltNameCy();
        this.displayName = areaOfLaw.getDisplayName();
        this.displayNameCy = areaOfLaw.getDisplayNameCy();
        this.displayExternalLink = areaOfLaw.getDisplayExternalLink();
    }
}
