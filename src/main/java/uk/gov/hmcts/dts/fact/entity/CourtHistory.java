package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Entity representation of CourtHistory.
 */
@Entity
@Table(name = "admin_court_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourtHistory {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "admin_courtlock_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;

    @Column(name = "search_court_id")
    private Integer searchCourtId;

    @Column(name = "court_name")
    private String courtName;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "court_name_cy")
    private String courtNameCy;


    public CourtHistory(uk.gov.hmcts.dts.fact.model.admin.CourtHistory courtHistory) {
        this.searchCourtId = courtHistory.getSearchCourtId();
        this.courtName = courtHistory.getCourtName();
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
        this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        this.courtNameCy = courtHistory.getCourtNameCy();
    }
}
