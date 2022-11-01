package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.AuditType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourtLock {
    private int id;
    @JsonProperty("lock_acquired")
    private LocalDateTime lockAcquired;
    @JsonProperty("user_email")
    private String userEmail;
    @JsonProperty("court_slug")
    private String courtSlug;

    public CourtLock(uk.gov.hmcts.dts.fact.entity.CourtLock courtLock) {
        this.id = courtLock.getId();
        this.lockAcquired = courtLock.getLockAcquired();
        this.userEmail = courtLock.getUserEmail();
        this.courtSlug = courtLock.getCourtSlug();
    }

    public CourtLock(LocalDateTime lockAcquired, String courtSlug, String userEmail) {
        this.lockAcquired = lockAcquired;
        this.userEmail = userEmail;
        this.courtSlug = courtSlug;
    }
}
