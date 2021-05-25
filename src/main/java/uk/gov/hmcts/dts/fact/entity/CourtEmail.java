package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import javax.persistence.*;

@Entity
@Table(name = "search_courtemail")
@Getter
@Setter
@NoArgsConstructor
public class CourtEmail {
    @Id()
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_courtemail_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email_id")
    private Email email;
    private Integer order;

    public CourtEmail(Court court, Email email, Integer order) {
        this.court = court;
        this.email = email;
        this.order = order;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void updateTimestamp() {
        court.setUpdatedAt(Timestamp.from(Instant.now()));
    }
}
