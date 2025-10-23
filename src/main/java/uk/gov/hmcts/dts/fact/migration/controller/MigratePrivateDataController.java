package uk.gov.hmcts.dts.fact.migration.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.migration.model.MigrationExportResponse;
import uk.gov.hmcts.dts.fact.migration.service.MigrationPrivateDataService;

@RestController
@RequestMapping("/private-migration/export")
public class MigratePrivateDataController {

    private final MigrationPrivateDataService migrationPrivateDataService;

    public MigratePrivateDataController(final MigrationPrivateDataService migrationPrivateDataService) {
        this.migrationPrivateDataService = migrationPrivateDataService;
    }

    @GetMapping
    @Operation(summary = "Return private migration data for a court")
    public MigrationExportResponse migratePrivateData() {
        return migrationPrivateDataService.getCourtExport();
    }
}
