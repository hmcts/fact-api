package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
