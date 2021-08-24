package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "audit_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    @Column(name = "user_email")
    private String userEmail;
    private String action;
    @Column(name = "action_data")
    private String actionData;
    private String location;
    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    public Audit(String userEmail, String action, String actionData,
                 String location, LocalDateTime creationTime) {
        this.userEmail = userEmail;
        this.action = action;
        this.actionData = actionData;
        this.location = location;
        this.creationTime = creationTime;
    }
}
