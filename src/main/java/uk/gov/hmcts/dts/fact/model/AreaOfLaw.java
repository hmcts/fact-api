package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data

public class AreaOfLaw {
    private String name;
    @JsonProperty("external_link")
    private String externalLink;
    @JsonProperty("display_url")
    private String displayUrl;
    @JsonProperty("external_link_desc")
    private String externalLinkDescription;

    public AreaOfLaw(uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw) {
        this.name = areaOfLaw.getName();
        this.externalLink = areaOfLaw.getExternalLink();
        this.externalLinkDescription = areaOfLaw.getExternalLinkDescription();
    }
}
