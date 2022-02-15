package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_servicecentre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCentre {

    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_servicecentre_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    @OneToOne
    @JoinColumn(name = "court_id")
    private Court courtId; // use this as the one-to-one mapping for court_id to court_id
    private String introParagraph;
    private String introParagraphCy;
}
