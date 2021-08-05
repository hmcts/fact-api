package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_courtareaoflaw")
@Getter
@Setter
@NoArgsConstructor
public class CourtAreaOfLaw {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_courtareaoflaw_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;

    @OneToOne()
    @JoinColumn(name = "area_of_law_id")
    private AreaOfLaw areaOfLaw;

    @OneToOne()
    @JoinColumn(name = "court_id")
    private Court court;
    private Boolean singlePointOfEntry;

    public CourtAreaOfLaw(final AreaOfLaw areaOfLaw, final Court court, boolean singlePointOfEntry) {
        this.areaOfLaw = areaOfLaw;
        this.court = court;
        this.singlePointOfEntry = singlePointOfEntry;
    }
}
