package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "search_courtareaoflaw")
public class CourtAreaOfLaw {
    @Id
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "area_of_law_id")
    private AreaOfLaw areaOfLaw;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "court_id")
    private Court court;
    private Boolean singlePointOfEntry;
}
