package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
