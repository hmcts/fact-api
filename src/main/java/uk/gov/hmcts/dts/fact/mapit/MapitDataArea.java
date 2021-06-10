package uk.gov.hmcts.dts.fact.mapit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapitDataArea {
    private String areaId;
    private MapitDataAreaDetails mapitDataAreaDetailsList;
}
