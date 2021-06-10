package uk.gov.hmcts.dts.fact.mapit;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapitDataAreaCode {

    private String unitId;
    private String ons;
    private String gss;
    private String localAuthorityCanonical;
    private String localAuthorityEng;
}
