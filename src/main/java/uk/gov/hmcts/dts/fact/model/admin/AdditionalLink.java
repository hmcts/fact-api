package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalLink {
    private Integer sidebarLocationId;
    private String url;
    private String description;
    private String descriptionCy;

    public AdditionalLink(final uk.gov.hmcts.dts.fact.entity.AdditionalLink additionalLink) {
        this.sidebarLocationId = additionalLink.getLocation().getId();
        this.url = additionalLink.getUrl();
        this.description = additionalLink.getDescription();
        this.descriptionCy = additionalLink.getDescriptionCy();
    }
}
