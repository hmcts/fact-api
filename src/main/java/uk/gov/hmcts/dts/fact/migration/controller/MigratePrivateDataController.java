package uk.gov.hmcts.dts.fact.migration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.migration.model.MigrationExportResponse;
import uk.gov.hmcts.dts.fact.migration.service.MigrationPrivateDataService;

/**
 * REST controller exposing the private data migration export endpoint.
 */
@RestController
@RequestMapping("/private-migration/export")
@Tag(name = "Private Migration")
public class MigratePrivateDataController {

    private final MigrationPrivateDataService migrationPrivateDataService;

    /**
     * Construct the controller with the underlying migration service.
     * @param migrationPrivateDataService service providing migration data aggregation
     */
    public MigratePrivateDataController(final MigrationPrivateDataService migrationPrivateDataService) {
        this.migrationPrivateDataService = migrationPrivateDataService;
    }

    /**
     * Retrieve the current private migration export payload.
     * @return migration export response containing courts and reference data
     */
    @GetMapping
    @Operation(
        summary = "Return private migration data for a court",
        description = "Aggregates court and reference data required for private migration clients."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Migration export payload",
        content = @Content(schema = @Schema(implementation = MigrationExportResponse.class))
    )
    public MigrationExportResponse migratePrivateData() {
        return migrationPrivateDataService.getCourtExport();
    }
}
