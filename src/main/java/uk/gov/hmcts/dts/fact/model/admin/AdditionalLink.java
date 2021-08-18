package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalLink {
    private String url;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("display_name_cy")
    private String displayNameCy;

    public AdditionalLink(final uk.gov.hmcts.dts.fact.entity.AdditionalLink additionalLink) {
        this.url = additionalLink.getUrl();
        this.displayName = additionalLink.getDescription();
        this.displayNameCy = additionalLink.getDescriptionCy();
    }
}

