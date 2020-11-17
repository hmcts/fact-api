package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;


@Data
@NoArgsConstructor
public class ServiceAreaWithCourtReferencesWithDistance {
    private String slug;
    private String name;
    private String onlineText;
    private String onlineUrl;
    private List<CourtReferenceWithDistance> courts;

    public ServiceAreaWithCourtReferencesWithDistance(final String slug) {
        this.slug = slug;
    }

    public ServiceAreaWithCourtReferencesWithDistance(final uk.gov.hmcts.dts.fact.entity.ServiceArea serviceArea, final List<CourtReferenceWithDistance> courts) {
        this.slug = serviceArea.getSlug();
        this.name = chooseString(serviceArea.getNameCy(), serviceArea.getName());
        this.onlineText = chooseString(serviceArea.getOnlineTextCy(), serviceArea.getOnlineText());
        this.onlineUrl = serviceArea.getOnlineUrl();
        this.courts = courts;
    }

}
