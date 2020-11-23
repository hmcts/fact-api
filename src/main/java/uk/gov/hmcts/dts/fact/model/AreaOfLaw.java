package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
public class AreaOfLaw {
    private String name;
    @JsonProperty("external_link")
    private String externalLink;
    @JsonProperty("display_url")
    private String displayUrl;
    @JsonProperty("external_link_desc")
    private String externalLinkDescription;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("display_external_link")
    private String displayExternalLink;

    public AreaOfLaw(uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw) {
        this.name = areaOfLaw.getName();
        this.externalLink = chooseString(areaOfLaw.getExternalLinkCy(), areaOfLaw.getExternalLink());
        this.externalLinkDescription =
            chooseString(areaOfLaw.getExternalLinkDescriptionCy(), areaOfLaw.getExternalLinkDescription());
        this.displayName = chooseString(areaOfLaw.getDisplayNameCy(), areaOfLaw.getDisplayName());
        this.displayExternalLink = areaOfLaw.getDisplayExternalLink();
    }
}
