package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_courtapplicationupdate")
@Getter
@Setter
@NoArgsConstructor
public class CourtApplicationUpdate {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_courtapplicationupdate_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "application_update_id")
    private ApplicationUpdate applicationUpdate;
    private Integer sort;
}
