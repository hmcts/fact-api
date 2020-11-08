package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "search_serviceareacourt")
@Getter
@Setter
public class ServiceAreaCourt {
    @Id
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "servicearea_id")
    private ServiceArea servicearea;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "court_id")
    private Court court;

    private String catchmentType;
}
