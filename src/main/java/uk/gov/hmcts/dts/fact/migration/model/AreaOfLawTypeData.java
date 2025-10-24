package uk.gov.hmcts.dts.fact.migration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuppressWarnings("ClassCanBeRecord") // Keep Lombok POJO until Checkstyle handles records
public class AreaOfLawTypeData {
    private final Integer id;
    private final String name;
    private final String externalLink;
    private final String externalLinkCy;
    private final String externalLinkDescription;
    private final String externalLinkDescriptionCy;
    private final String altName;
    private final String altNameCy;
    private final String displayName;
    private final String displayNameCy;
    private final String displayExternalLink;
}
