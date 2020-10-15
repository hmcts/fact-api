package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_servicearea")
@Getter
@Setter
public class ServiceArea {
    @Id
    private Integer id;
    private String serviceArea;
    @OneToOne
    @JoinColumn(name = "court_id")
    private Court courtId;
}
