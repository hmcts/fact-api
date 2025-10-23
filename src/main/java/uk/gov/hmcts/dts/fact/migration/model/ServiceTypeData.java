package uk.gov.hmcts.dts.fact.migration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ServiceTypeData {
    private final Integer id;
    private final String name;
    private final String nameCy;
    private final String description;
    private final String descriptionCy;
    private final String slug;
}

