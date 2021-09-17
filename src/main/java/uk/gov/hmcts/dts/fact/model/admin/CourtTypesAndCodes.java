package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CourtTypesAndCodes {

    @JsonProperty("types")
    private List<CourtType> courtTypes;

    private String gbsCode;

    private List<DxCode> dxCodes;


}
