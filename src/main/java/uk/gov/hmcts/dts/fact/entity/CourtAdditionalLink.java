package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import javax.persistence.*;

@Entity
@Table(name = "search_courtadditionallink")
@Getter
@Setter
@NoArgsConstructor
public class CourtAdditionalLink {
    @Id
    @SequenceGenerator(name = "seq-gen-links", sequenceName = "search_courtadditionallink_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-links")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "additional_link_id")
    private AdditionalLink additionalLink;
    private Integer sort;

    public CourtAdditionalLink(final Court court, final AdditionalLink additionalLink, final Integer sort) {
        this.court = court;
        this.additionalLink = additionalLink;
        this.sort = sort;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void updateTimestamp() {
        court.setUpdatedAt(Timestamp.from(Instant.now()));
    }
}
