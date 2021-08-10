package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalLink {
    @JsonProperty("location_id")
    private Integer sidebarLocationId;
    private String url;
    @JsonProperty("url_description")
    private String urlDescription;
    @JsonProperty("url_description_cy")
    private String urlDescriptionCy;
    private String type;

    public AdditionalLink(final uk.gov.hmcts.dts.fact.entity.AdditionalLink additionalLink) {
        this.sidebarLocationId = additionalLink.getLocation().getId();
        this.url = additionalLink.getUrl();
        this.urlDescription = additionalLink.getDescription();
        this.urlDescriptionCy = additionalLink.getDescriptionCy();
        this.type = additionalLink.getType();
    }
}
