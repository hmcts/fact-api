package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "search_courtopeningtime")
@Getter
@Setter
@NoArgsConstructor
public class CourtOpeningTime {
    @Id()
    @SequenceGenerator(name = "seq-gen-court-opening", sequenceName = "search_courtopeningtime_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-court-opening")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "opening_time_id")
    private OpeningTime openingTime;
    private Integer sort;

    public CourtOpeningTime(final Court court, final OpeningTime openingTime, final Integer sort) {
        this.court = court;
        this.openingTime = openingTime;
        this.sort = sort;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void updateTimestamp() {
        court.setUpdatedAt(Timestamp.from(Instant.now()));
    }
}
