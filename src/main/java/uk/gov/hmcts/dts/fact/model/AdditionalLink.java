package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
public class AdditionalLink {
    private String url;
    private String description;

    public AdditionalLink(uk.gov.hmcts.dts.fact.entity.AdditionalLink additionalLink) {
        this.url = additionalLink.getUrl();
        this.description = chooseString(additionalLink.getDescriptionCy(), additionalLink.getDescription());
    }
}
