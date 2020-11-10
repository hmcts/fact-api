package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "search_servicearea")
@Getter
@Setter
public class ServiceArea {
    @Id
    private Integer id;
    private String name;
    private String nameCy;
    private String description;
    private String descriptionCy;
    private String slug;
    private String onlineUrl;
    private String onlineText;
    private String onlineTextCy;
    private String serviceAreaType;
    private String applicationType;

    @OneToMany(mappedBy = "servicearea")
    private List<ServiceAreaCourt> serviceAreaCourts;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "area_of_law_id")
    private AreaOfLaw areaOfLaw;
}
