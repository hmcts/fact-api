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
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAreasOfLawService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_VIEWER;

/**
 * Controller for retrieving and updating court areas of law.
 */
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtAreasOfLawController {
    private final AdminCourtAreasOfLawService adminCourtAreasOfLawService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Construct a new AdminCourtAreasOfLawController.
     * @param adminService the admin court areas of law service
     * @param adminCourtLockService the admin court lock service
     */
    @Autowired
    public AdminCourtAreasOfLawController(AdminCourtAreasOfLawService adminService,
                                          AdminCourtLockService adminCourtLockService) {
        this.adminCourtAreasOfLawService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Retrieves areas of law for a specific court.
     * @param slug Court slug
     * @return A list of court areas of law
     */
    @GetMapping(path = "/{slug}/courtAreasOfLaw")
    @Operation(summary = "Find the areas of law for a court")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<AreaOfLaw>> getCourtAreasOfLaw(@PathVariable String slug) {
        return ok(adminCourtAreasOfLawService.getCourtAreasOfLawBySlug(slug));
    }

    /**
     * Update the areas of law for a court.
     * @param slug Court slug
     * @param areasOfLaw A list of areas of law
     * @return A list of court areas of law
     */
    @PutMapping(path = "/{slug}/courtAreasOfLaw")
    @Operation(summary = "Update the areas of law for a court")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<AreaOfLaw>> updateCourtAreasOfLaw(@PathVariable String slug,
                                                                 @RequestBody List<AreaOfLaw> areasOfLaw,
                                                                 Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminCourtAreasOfLawService.updateAreasOfLawForCourt(slug, areasOfLaw));
    }
}
