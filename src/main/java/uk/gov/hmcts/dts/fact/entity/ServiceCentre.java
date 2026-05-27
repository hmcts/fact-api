package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "search_servicecentre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCentre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @OneToOne
    @JoinColumn(name = "court_id")
    private Court courtId; // use this as the one-to-one mapping for court_id to court_id
    @Column(name = "intro_paragraph")
    private String introParagraph;

    @Column(name = "intro_paragraph_cy")
    private String introParagraphCy;
}
