package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_courtadditionallink")
@Getter
@Setter
@NoArgsConstructor
public class CourtAdditionalLink {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_courtadditionallink_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "additional_link_id")
    private AdditionalLink additionalLink;
    private Integer sort;
}
