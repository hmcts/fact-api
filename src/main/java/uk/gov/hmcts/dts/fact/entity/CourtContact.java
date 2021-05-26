package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
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
    private Contact contact;
    private Integer sortOrder;

    public CourtContact(final Court court, final Contact contact, final Integer sortOrder) {
        this.court = court;
        this.contact = contact;
        this.sortOrder = sortOrder;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void updateTimestamp() {
        court.setUpdatedAt(Timestamp.from(Instant.now()));
    }
}
