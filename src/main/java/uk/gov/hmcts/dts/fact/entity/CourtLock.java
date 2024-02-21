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

@Entity
@Table(name = "admin_courtlock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourtLock {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "admin_courtlock_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    @Column(name = "lock_acquired")
    private LocalDateTime lockAcquired;
    private String userEmail;
    private String courtSlug;

    public CourtLock(uk.gov.hmcts.dts.fact.model.admin.CourtLock courtLock) {
        this.lockAcquired = LocalDateTime.now(ZoneOffset.UTC);
        this.userEmail = courtLock.getUserEmail();
        this.courtSlug = courtLock.getCourtSlug();
    }

    @Override
    public String toString() {
        return "CourtLock{"
            + "lockAcquired=" + lockAcquired
            + ", userEmail='" + userEmail + '\''
            + ", courtSlug='" + courtSlug + '\''
            + '}';
    }
}
