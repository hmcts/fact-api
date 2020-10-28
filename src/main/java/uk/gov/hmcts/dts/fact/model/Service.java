package uk.gov.hmcts.dts.fact.model;

import lombok.Data;

import java.util.List;

import static java.util.stream.Collectors.toList;


@Data
public class Service {
    private String name;
    private String description;
    private List<ServiceArea> serviceAreas;

    public Service(uk.gov.hmcts.dts.fact.entity.Service service) {
        this.name = service.getName();
        this.description = service.getDescription();
        this.serviceAreas = service.getServiceAreas().stream().map(ServiceArea::new).collect(toList());
    }
}
