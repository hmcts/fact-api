package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_courtpostcode")
@Getter
@Setter
@NoArgsConstructor
public class CourtPostcode {
    @Id()
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_courtpostcode_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    private String postcode;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;

    public CourtPostcode(final String postcode, final Court court) {
        this.postcode = postcode;
        this.court = court;
    }
}
