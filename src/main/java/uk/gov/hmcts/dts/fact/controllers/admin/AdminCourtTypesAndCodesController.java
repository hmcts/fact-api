package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.model.admin.CourtTypesAndCodes;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtTypesAndCodesService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_VIEWER;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtTypesAndCodesController {
    private final AdminCourtTypesAndCodesService courtTypesAndCodesService;
    private final AdminCourtLockService adminCourtLockService;

    @Autowired
    public AdminCourtTypesAndCodesController(AdminCourtTypesAndCodesService adminService,
                                             AdminCourtLockService adminCourtLockService) {
        this.courtTypesAndCodesService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    @GetMapping(path = "/courtTypes")
    @Operation(summary = "Return all court types")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtType>> getAllCourtTypes() {
        return ok(courtTypesAndCodesService.getAllCourtTypes());
    }

    @GetMapping(path = "/{slug}/courtTypesAndCodes")
    @Operation(summary = "Find a court's types, GBS code and Dx codes by slug")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtTypesAndCodes> getCourtTypesAndCodes(@PathVariable String slug) {
        return ok(courtTypesAndCodesService.getCourtTypesAndCodes(slug));
    }

    @PutMapping(path = "/{slug}/courtTypesAndCodes")
    @Operation(summary = "Update a court's types, GBS code and Dx codes")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtTypesAndCodes> updateCourtTypesAndCodes(@PathVariable String slug,
                                                                       @RequestBody CourtTypesAndCodes courtTypesAndCodes,
                                                                       Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(courtTypesAndCodesService.updateCourtTypesAndCodes(slug, courtTypesAndCodes));
    }
}
