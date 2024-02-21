package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "search_courtareaoflaw")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CourtAreaOfLaw {
    @Id
    @SequenceGenerator(name = "seq-gen-caol", sequenceName = "search_courtareaoflaw_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-caol")
    private Integer id;

    @OneToOne()
    @JoinColumn(name = "area_of_law_id")
    private AreaOfLaw areaOfLaw;

    @OneToOne()
    @JoinColumn(name = "court_id")
    private Court court;

    public CourtAreaOfLaw(final AreaOfLaw areaOfLaw, final Court court) {
        this.areaOfLaw = areaOfLaw;
        this.court = court;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void updateTimestamp() {
        court.setUpdatedAt(Timestamp.from(Instant.now()));
    }
}
