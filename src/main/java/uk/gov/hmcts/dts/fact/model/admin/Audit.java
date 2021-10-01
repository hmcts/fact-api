package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.AuditType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Audit {
    private int id;
    @JsonProperty("user_email")
    private String userEmail;
    private AuditType action;
    @JsonProperty("action_data_before")
    private String actionDataBefore;
    @JsonProperty("action_data_after")
    private String actionDataAfter;
    private String location;
    @JsonProperty("creation_time")
    private LocalDateTime creationTime;

    public Audit(uk.gov.hmcts.dts.fact.entity.Audit audit) {
        this.id = audit.getId();
        this.userEmail = audit.getUserEmail();
        this.action = audit.getAuditType();
        this.actionDataBefore = audit.getActionDataBefore();
        this.actionDataAfter = audit.getActionDataAfter();
        this.location = audit.getLocation();
        this.creationTime = audit.getCreationTime();
    }
}
