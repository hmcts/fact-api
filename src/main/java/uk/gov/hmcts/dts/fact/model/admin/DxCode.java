package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DxCode {

    private String code;
    private String explanation;
    private String explanationCy;

    public DxCode(final uk.gov.hmcts.dts.fact.entity.DxCode dxCode) {
        this.code = dxCode.getCode();
        this.explanation = dxCode.getExplanation();
        this.explanationCy = dxCode.getExplanationCy();
    }
}
