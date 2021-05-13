package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "search_courtcontact")
@Getter
@Setter
@NoArgsConstructor
public class CourtContact {
    @Id()
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_courtcontact_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_id")
    @OrderBy("sort_order")
    private Contact contact;

    public CourtContact(final Court court, final Contact contact) {
        this.court = court;
        this.contact = contact;
    }
}
