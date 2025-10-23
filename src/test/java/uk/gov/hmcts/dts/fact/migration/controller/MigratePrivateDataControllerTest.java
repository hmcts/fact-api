package uk.gov.hmcts.dts.fact.migration.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.dts.fact.migration.model.CourtMigrationData;
import uk.gov.hmcts.dts.fact.migration.model.MigrationExportResponse;
import uk.gov.hmcts.dts.fact.migration.service.MigrationPrivateDataService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MigratePrivateDataControllerTest {

    private static final String SLUG = "test-slug";

    @Mock
    private MigrationPrivateDataService migrationPrivateDataService;

    @InjectMocks
    private MigratePrivateDataController migratePrivateDataController;

    @Test
    void shouldDelegateToServiceAndReturnResponse() {
        CourtMigrationData court = new CourtMigrationData(
            "12",
            "Test Court",
            SLUG,
            Boolean.TRUE,
            "notice",
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            9,
            Boolean.TRUE
        );
        MigrationExportResponse expected = new MigrationExportResponse(List.of(court));
        when(migrationPrivateDataService.getCourtExport()).thenReturn(expected);

        MigrationExportResponse response = migratePrivateDataController.migratePrivateData();

        assertThat(response).isEqualTo(expected);
        verify(migrationPrivateDataService).getCourtExport();
    }
}
