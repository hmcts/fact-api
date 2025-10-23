package uk.gov.hmcts.dts.fact.migration.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.dts.fact.migration.model.AreaOfLawTypeData;
import uk.gov.hmcts.dts.fact.migration.model.ContactDescriptionTypeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtAreasOfLawData;
import uk.gov.hmcts.dts.fact.migration.model.CourtCodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtDxCodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtFaxData;
import uk.gov.hmcts.dts.fact.migration.model.CourtMigrationData;
import uk.gov.hmcts.dts.fact.migration.model.CourtPhotoData;
import uk.gov.hmcts.dts.fact.migration.model.CourtPostcodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtServiceAreaData;
import uk.gov.hmcts.dts.fact.migration.model.CourtSinglePointOfEntryData;
import uk.gov.hmcts.dts.fact.migration.model.CourtTypeData;
import uk.gov.hmcts.dts.fact.migration.model.LocalAuthorityTypeData;
import uk.gov.hmcts.dts.fact.migration.model.MigrationExportResponse;
import uk.gov.hmcts.dts.fact.migration.model.OpeningHourTypeData;
import uk.gov.hmcts.dts.fact.migration.model.RegionData;
import uk.gov.hmcts.dts.fact.migration.model.ServiceAreaTypeData;
import uk.gov.hmcts.dts.fact.migration.model.ServiceTypeData;
import uk.gov.hmcts.dts.fact.migration.service.MigrationPrivateDataService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MigratePrivateDataControllerTest {

    @Mock
    private MigrationPrivateDataService migrationPrivateDataService;

    @InjectMocks
    private MigratePrivateDataController migratePrivateDataController;

    @Test
    void shouldDelegateToServiceAndReturnResponse() {
        CourtMigrationData court = new CourtMigrationData(
            "12",
            "Test Court",
            "test-slug",
            Boolean.TRUE,
            "notice",
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            9,
            Boolean.TRUE,
            List.of(new CourtServiceAreaData(50, List.of(2, 3), 12, "regional")),
            List.of(new CourtPostcodeData(100, "AB1 2CD", 12)),
            new CourtCodeData("12", "12", 3333, 4444, 5555, 2222, 1111, "GBS123"),
            new CourtAreasOfLawData("70", List.of(9), "12"),
            new CourtSinglePointOfEntryData("80", List.of(9), "12"),
            List.of(new CourtDxCodeData("120", "12", "DX123", "DX explanation")),
            List.of(new CourtFaxData("90", "12", "0123456789")),
            new CourtPhotoData("https://factaat.blob.core.windows.net/images/court.jpg")
        );
        MigrationExportResponse expected = new MigrationExportResponse(
            List.of(court),
            List.of(new LocalAuthorityTypeData(1, "Authority")),
            List.of(new ServiceAreaTypeData(2, "Service Area", "Service Area Cy", "desc", "desc cy",
                "service-area", "http://example.com", "online", "online cy", "type", "text",
                "text cy", "catchment", 6)),
            List.of(new ServiceTypeData(3, "Service", "Service Cy", "service desc", "service desc cy", "service")),
            List.of(new ContactDescriptionTypeData(4, "Phone", "Phone cy")),
            List.of(new OpeningHourTypeData(5, "Opening", "Opening cy")),
            List.of(new CourtTypeData(7, "Court Type", "court-type")),
            List.of(new RegionData(8, "Region", "England")),
            List.of(new AreaOfLawTypeData(9, "Area", "http://external", "http://external-cy", "desc",
                "desc cy", "alt", "alt cy", "display", "display cy", "Y"))
        );
        when(migrationPrivateDataService.getCourtExport()).thenReturn(expected);

        MigrationExportResponse response = migratePrivateDataController.migratePrivateData();

        assertThat(response).isEqualTo(expected);
        verify(migrationPrivateDataService).getCourtExport();
    }
}
