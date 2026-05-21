package uk.gov.hmcts.dts.fact.migration.model;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuppressWarnings("ClassCanBeRecord") // Keep Lombok POJO until Checkstyle handles records
public class ServiceAreaTypeData {
    private final Integer id;
    private final String name;
    private final String nameCy;
    private final String description;
    private final String descriptionCy;
    private final String onlineUrl;
    private final String onlineText;
    private final String onlineTextCy;
    private final String type;
    private final String text;
    private final String textCy;
    private final String catchmentMethod;
    private final Integer areaOfLawId;
}
