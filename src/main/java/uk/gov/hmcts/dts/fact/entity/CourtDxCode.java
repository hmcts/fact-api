package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "search_courtdxcode")
@Getter
@Setter
@NoArgsConstructor
public class CourtDxCode {
    @Id
    @SequenceGenerator(name = "seq-gen-dx-code", sequenceName = "search_courtdxcode_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-dx-code")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "dx_code_id")
    private DxCode dxCode;
    private Integer sort;

    public CourtDxCode(final Court court, final DxCode dxCode) {
        this.court = court;
        this.dxCode = dxCode;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void updateTimestamp() {
        court.setUpdatedAt(Timestamp.from(Instant.now()));
    }
}
