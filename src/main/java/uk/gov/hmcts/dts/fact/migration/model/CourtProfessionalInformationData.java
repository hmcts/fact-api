package uk.gov.hmcts.dts.fact.migration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
