package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_courtareaoflawspoe")
@Getter
@Setter
@NoArgsConstructor
public class CourtAreaOfLawSpoe {
    @Id
    @SequenceGenerator(name = "seq-gen-aol-spoe", sequenceName = "search_courtareaoflawspoe_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-aol-spoe")
    private Integer id;

    @OneToOne()
    @JoinColumn(name = "area_of_law_id")
    private AreaOfLaw areaOfLaw;

    @OneToOne()
    @JoinColumn(name = "court_id")
    private Court court;

    public CourtAreaOfLawSpoe(final AreaOfLaw areaOfLaw, final Court court) {
        this.areaOfLaw = areaOfLaw;
        this.court = court;
    }
}
