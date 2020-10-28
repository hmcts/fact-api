package uk.gov.hmcts.dts.fact.model;

import lombok.Data;


@Data
public class ServiceArea {
    private String name;
    private String description;

    public ServiceArea(uk.gov.hmcts.dts.fact.entity.ServiceArea serviceArea) {
        this.name = serviceArea.getName();
        this.description = serviceArea.getDescription();
    }
}
