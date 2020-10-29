package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "search_servicearea")
@Getter
@Setter
public class ServiceArea {
    @Id
    private Integer id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;
}
