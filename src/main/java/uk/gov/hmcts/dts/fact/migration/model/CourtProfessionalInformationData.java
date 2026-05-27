package uk.gov.hmcts.dts.fact.migration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuppressWarnings("ClassCanBeRecord") // Keep Lombok POJO until Checkstyle handles records
public class CourtProfessionalInformationData {
    private final Boolean interviewRooms;
    private final Integer interviewRoomCount;
    private final String interviewPhoneNumber;
    private final Boolean videoHearings;
    private final Boolean commonPlatform;
    private final Boolean accessScheme;
}
