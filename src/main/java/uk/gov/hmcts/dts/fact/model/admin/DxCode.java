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

    private Integer id;
    private String code;
    private String explanation;
    private String explanationCy;

    public DxCode(final String code, final String explanation, final String explanationCy) {
        this.code = code;
        this.explanation = explanation;
        this.explanationCy = explanationCy;
    }
}
