package uk.gov.hmcts.dts.fact.migration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CourtServiceAreaData {
    private final Integer id;
    @JsonProperty("service_area_ids")
    private final List<Integer> serviceAreaIds;
    private final Integer courtId;
    private final String catchmentType;
}

