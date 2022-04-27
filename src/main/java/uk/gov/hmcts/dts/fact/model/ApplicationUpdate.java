package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ApplicationUpdate {
    private String type;
    private String typeCy;
    private String email;
    @JsonProperty("external_link")
    private String externalLink;
    @JsonProperty("external_link_description")
    private String externalLinkDescription;
    @JsonProperty("external_link_description_cy")
    private String externalLinkDescriptionCy;

    public ApplicationUpdate(uk.gov.hmcts.dts.fact.entity.ApplicationUpdate applicationUpdate) {
        super();
        this.type = applicationUpdate.getType();
        this.typeCy = applicationUpdate.getTypeCy();
        this.email = applicationUpdate.getEmail();
        this.externalLink = applicationUpdate.getExternalLink();
        this.externalLinkDescription = applicationUpdate.getExternalLinkDescription();
        this.externalLinkDescriptionCy = applicationUpdate.getExternalLinkDescriptionCy();
    }
}
