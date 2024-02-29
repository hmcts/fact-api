package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admin_audittype")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditType {
    @Id
    private Integer id;
    private String name;
}

