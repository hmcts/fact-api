package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "search_courtpostcode")
@Getter
@Setter
public class CourtPostcode {
    @Id
    private Integer id;
    private String postcode;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
}
