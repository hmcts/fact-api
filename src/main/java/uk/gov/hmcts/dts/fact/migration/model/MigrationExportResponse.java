package uk.gov.hmcts.dts.fact.migration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuppressWarnings("ClassCanBeRecord") // Keep Lombok POJO until Checkstyle handles records
public class MigrationExportResponse {
    private final List<CourtMigrationData> courts;
    private final List<LocalAuthorityTypeData> localAuthorityTypes;
    private final List<ServiceAreaTypeData> serviceAreas;
    private final List<ServiceTypeData> services;
    private final List<ContactDescriptionTypeData> contactDescriptionTypes;
    private final List<OpeningHourTypeData> openingHourTypes;
    private final List<CourtTypeData> courtTypes;
    private final List<RegionData> regions;
    private final List<AreaOfLawTypeData> areaOfLawTypes;
}
