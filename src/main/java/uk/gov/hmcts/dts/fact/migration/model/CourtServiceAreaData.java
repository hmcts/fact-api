package uk.gov.hmcts.dts.fact.migration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuppressWarnings("ClassCanBeRecord") // Keep Lombok POJO until Checkstyle handles records
public class CourtServiceAreaData {
    private final Integer id;
    @JsonProperty("service_area_ids")
    private final List<Integer> serviceAreaIds;
    private final String catchmentType;
}
