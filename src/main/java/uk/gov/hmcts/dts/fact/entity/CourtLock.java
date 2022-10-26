package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

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
}
