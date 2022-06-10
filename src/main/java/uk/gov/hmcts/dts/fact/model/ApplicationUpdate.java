package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ApplicationUpdate {
    private String type;
    private String email;
    @JsonProperty("external_link")
    private String externalLink;
    @JsonProperty("external_link_description")
    private String externalLinkDescription;

    public ApplicationUpdate(uk.gov.hmcts.dts.fact.entity.ApplicationUpdate applicationUpdate) {
        super();
        this.type = chooseString(applicationUpdate.getTypeCy(), applicationUpdate.getType());
        this.email = applicationUpdate.getEmail();
        this.externalLink = applicationUpdate.getExternalLink();
        this.externalLinkDescription = chooseString(applicationUpdate.getExternalLinkDescriptionCy(),
                                                    applicationUpdate.getExternalLinkDescription());
    }
}
