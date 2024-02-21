package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    @OrderBy("sort_order")
    private List<ServiceArea> serviceAreas;
}
