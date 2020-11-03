package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "search_service")
@Getter
@Setter
public class Service {
    @Id
    private Integer id;
    private String name;
    private String nameCy;
    private String description;
    private String descriptionCy;
    private String slug;
    @OneToMany(mappedBy = "service")
    private List<ServiceArea> serviceAreas;
}
