package uk.gov.hmcts.dts.fact.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public CourtAreaOfLaw(final AreaOfLaw areaOfLaw, final Court court) {
        this.areaOfLaw = areaOfLaw;
        this.court = court;

//      Single point of entry needs to be set.
//      This will be looked at in another story but will be set to false by default for now.
        this.singlePointOfEntry = getSinglePointOfEntry();
    }

}
