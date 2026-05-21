package uk.gov.hmcts.dts.fact.migration.model;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuppressWarnings("ClassCanBeRecord") // Keep Lombok POJO until Checkstyle handles records
public class ServiceTypeData {
    private final Integer id;
    private final String name;
    private final String nameCy;
    private final String description;
    private final String descriptionCy;
    private final List<Integer> serviceAreaIds;
}
