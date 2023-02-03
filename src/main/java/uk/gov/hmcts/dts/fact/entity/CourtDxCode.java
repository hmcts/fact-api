package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
