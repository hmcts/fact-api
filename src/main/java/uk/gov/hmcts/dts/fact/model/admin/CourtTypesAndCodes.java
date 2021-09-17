package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Getter
public class CourtTypesAndCodes {

    @JsonProperty("types")
    private List<CourtType> courtTypes;

    private String gbsCode;

    private List<DxCode> dxCodes;

    public CourtTypesAndCodes(final List<CourtType> courtTypes,final String gbsCode,final List<DxCode> dxCodes) {

        this.courtTypes = courtTypes;
        this.gbsCode = gbsCode;
        this.dxCodes = dxCodes;
    }

}
