package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "search_inperson")
@Getter
@Setter
public class InPerson {
    @Id
    private Integer id;
    private Boolean isInPerson;
    @OneToOne
    @JoinColumn(name = "court_id")
    private Court courtId;
    private Boolean accessScheme;
}
