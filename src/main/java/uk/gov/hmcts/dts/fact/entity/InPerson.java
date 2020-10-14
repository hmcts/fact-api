package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_inperson")
@Getter
@Setter
public class InPerson {
    @Id
    private Integer id;
    private Boolean inPerson;
    @OneToOne
    @JoinColumn(name = "court_id")
    private Court courtId;
}
