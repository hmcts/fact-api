package uk.gov.hmcts.dts.fact.migration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ServiceAreaTypeData {
    private final Integer id;
    private final String name;
    private final String nameCy;
    private final String description;
    private final String descriptionCy;
    private final String slug;
    private final String onlineUrl;
    private final String onlineText;
    private final String onlineTextCy;
    private final String type;
    private final String text;
    private final String textCy;
    private final String catchmentMethod;
    private final Integer areaOfLawId;
}

