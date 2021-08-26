package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "admin_audit_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    @Column(name = "user_email")
    private String userEmail;
    @OneToOne()
    @JoinColumn(name = "action_id")
    private AuditType auditType;
    @Column(name = "action_data_before")
    private String actionDataBefore;
    @Column(name = "action_data_after")
    private String actionDataAfter;
    private String location;
    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    public Audit(String userEmail, final AuditType auditType,
                 String actionDataBefore, String actionDataAfter,
                 LocalDateTime creationTime) {
        this.userEmail = userEmail;
        this.auditType = auditType;
        this.actionDataBefore = actionDataBefore;
        this.actionDataAfter = actionDataAfter;
        this.creationTime = creationTime;
    }

    public Audit(String userEmail, final AuditType auditType,
                 String actionDataBefore, String actionDataAfter,
                 String location, LocalDateTime creationTime) {
        this.userEmail = userEmail;
        this.auditType = auditType;
        this.actionDataBefore = actionDataBefore;
        this.actionDataAfter = actionDataAfter;
        this.location = location;
        this.creationTime = creationTime;
    }
}
