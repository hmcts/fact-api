package uk.gov.hmcts.dts.fact.migration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
