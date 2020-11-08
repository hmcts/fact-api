package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "search_service")
@Getter
@Setter
public class Service {
    private static final String SERVICE_ID = "service_id";
    @Id
    private Integer id;
    private String name;
    private String nameCy;
    private String description;
    private String descriptionCy;
    private String slug;

    @ManyToMany
    @JoinTable(
        name = "search_serviceservicearea",
        joinColumns = @JoinColumn(name = SERVICE_ID),
        inverseJoinColumns = @JoinColumn(name = "servicearea_id")
    )
    private List<ServiceArea> serviceAreas;
}
