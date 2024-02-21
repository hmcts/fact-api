package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "search_courtpostcode")
@Getter
@Setter
@NoArgsConstructor
public class CourtPostcode {
    @Id()
    @SequenceGenerator(name = "seq-gen-court-postcode", sequenceName = "search_courtpostcode_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-court-postcode")
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
