package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;


@Data
@NoArgsConstructor
public class ServiceArea {
    private String name;
    private String description;
    private String slug;
    private String serviceAreaType;
    private String onlineUrl;
    private String onlineText;
    private String areaOfLawName;
    private List<ServiceAreaCourt> serviceAreaCourts;
    private String text;

    public ServiceArea(uk.gov.hmcts.dts.fact.entity.ServiceArea serviceArea) {
        this.name = chooseString(serviceArea.getNameCy(), serviceArea.getName());
        this.description = chooseString(serviceArea.getDescriptionCy(), serviceArea.getDescription());
        this.slug = serviceArea.getSlug();
        this.onlineUrl = serviceArea.getOnlineUrl();
        this.onlineText = chooseString(serviceArea.getOnlineTextCy(), serviceArea.getOnlineText());
        this.serviceAreaType = serviceArea.getType();
        this.areaOfLawName = serviceArea.getAreaOfLaw().getName();
        this.serviceAreaCourts = serviceArea.getServiceAreaCourts() == null ? null : serviceArea.getServiceAreaCourts().stream().map(
            ServiceAreaCourt::new).collect(toList());
        this.text = chooseString(serviceArea.getTextCy(), serviceArea.getText());
    }
}
