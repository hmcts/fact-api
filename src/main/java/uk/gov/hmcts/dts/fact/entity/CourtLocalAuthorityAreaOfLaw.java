package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import jakarta.persistence.Entity;
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
@Table(name = "search_courtlocalauthorityareaoflaw")
@Getter
@Setter
@NoArgsConstructor
public class CourtLocalAuthorityAreaOfLaw {

    @Id
    @SequenceGenerator(name = "seq-gen-local-aol", sequenceName = "search_courtlocalauthorityareaoflaw_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-local-aol")
    private Integer id;

    @OneToOne()
    @JoinColumn(name = "area_of_law_id")
    private AreaOfLaw areaOfLaw;

    @OneToOne()
    @JoinColumn(name = "court_id")
    private Court court;

    @OneToOne()
    @JoinColumn(name = "local_authority_id")
    private LocalAuthority localAuthority;

    public CourtLocalAuthorityAreaOfLaw(final AreaOfLaw areaOfLaw, final Court court, LocalAuthority localAuthority) {
        this.areaOfLaw = areaOfLaw;
        this.court = court;
        this.localAuthority = localAuthority;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void updateTimestamp() {
        court.setUpdatedAt(Timestamp.from(Instant.now()));
    }
}
