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

    public AreaOfLaw(uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw, boolean welsh) {
        this.name = areaOfLaw.getName();
        this.externalLink = chooseString(welsh, areaOfLaw.getExternalLinkCy(), areaOfLaw.getExternalLink());
        this.externalLinkDescription =
            chooseString(welsh, areaOfLaw.getExternalLinkDescriptionCy(), areaOfLaw.getExternalLinkDescription());
    }
}
