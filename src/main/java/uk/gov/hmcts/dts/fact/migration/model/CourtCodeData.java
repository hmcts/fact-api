package uk.gov.hmcts.dts.fact.migration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CourtCodeData {
    private final String id;
    private final Integer magistrateCourtCode;
    private final Integer familyCourtCode;
    private final Integer tribunalCode;
    private final Integer countyCourtCode;
    private final Integer crownCourtCode;
    private final String gbs;
}
