package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "search_inperson")
@Getter
@Setter
public class InPerson {

    @Id
    @SequenceGenerator(name = "seq-in-person-gen", sequenceName = "public.search_inperson_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-in-person-gen")
    @Column(name = "id")
    private Integer id;

    @Column(name = "is_in_person")
    private Boolean isInPerson;
    @OneToOne
    @JoinColumn(name = "court_id")
    private Court courtId;
    @Column(name = "access_scheme")
    private Boolean accessScheme;

    @Column(name = "common_platform")
    private Boolean commonPlatform;
}
