package uk.gov.hmcts.dts.fact.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;


@Data
@NoArgsConstructor
public class Service {
    private String name;
    private String description;
    private List<ServiceArea> serviceAreas;

    public Service(uk.gov.hmcts.dts.fact.entity.Service service) {
        this.name = chooseString(service.getNameCy(), service.getName());
        this.description = chooseString(service.getDescriptionCy(), service.getDescription());
        this.serviceAreas = service.getServiceAreas() == null ? null : service.getServiceAreas().stream().map(
            ServiceArea::new).collect(toList());
    }
}
