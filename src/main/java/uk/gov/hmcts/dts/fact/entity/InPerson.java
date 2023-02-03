package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "search_inperson")
@Getter
@Setter
public class InPerson {

    @Id
    @SequenceGenerator(name = "seq-in-person-gen", sequenceName = "public.search_inperson_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-in-person-gen")
    private Integer id;

    private Boolean isInPerson;
    @OneToOne
    @JoinColumn(name = "court_id")
    private Court courtId;
    private Boolean accessScheme;
    private Boolean commonPlatform;
}
