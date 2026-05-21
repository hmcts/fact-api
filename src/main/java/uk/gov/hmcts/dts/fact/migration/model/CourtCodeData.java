package uk.gov.hmcts.dts.fact.migration.model;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuppressWarnings("ClassCanBeRecord") // Keep Lombok POJO until Checkstyle handles records
public class CourtCodeData {
    private final String id;
    private final Integer magistrateCourtCode;
    private final Integer familyCourtCode;
    private final Integer tribunalCode;
    private final Integer countyCourtCode;
    private final Integer crownCourtCode;
    private final String gbs;
}
