package uk.gov.hmcts.dts.fact.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "search_dxcode")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DxCode {
    @Id
    @SequenceGenerator(name = "dxcode-seq-gen", sequenceName = "search_dxcode_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dxcode-seq-gen")
    private Integer id;
    private String code;
    private String explanation;
    private String explanationCy;
    private boolean inLeaflet;

    public DxCode(final String code, final String explanation, final String explanationCy) {
        this.code = code;
        this.explanation = explanation;
        this.explanationCy = explanationCy;
        this.inLeaflet = true;
    }

}
