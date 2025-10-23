package uk.gov.hmcts.dts.fact.migration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CourtMigrationData {
    private final String id;
    private final String name;
    private final String slug;
    private final Boolean open;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime lastUpdatedAt;
    private final Integer regionId;
    @JsonProperty("is_service_centre")
    private final Boolean serviceCentre;
    private final List<CourtServiceAreaData> courtServiceAreas;
}
