package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import uk.gov.hmcts.dts.fact.entity.SidebarLocation;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
public class AdditionalLink {
    private String url;
    private String description;
    private String location;

    public AdditionalLink(uk.gov.hmcts.dts.fact.entity.AdditionalLink additionalLink) {
        this.url = additionalLink.getUrl();
        this.description = chooseString(additionalLink.getDescriptionCy(), additionalLink.getDescription());

        final SidebarLocation location = additionalLink.getLocation();
        this.location = StringUtils.isBlank(location.getName()) ? "" : location.getName();
    }
}
