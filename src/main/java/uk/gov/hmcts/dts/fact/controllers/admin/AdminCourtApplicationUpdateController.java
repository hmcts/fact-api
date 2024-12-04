package uk.gov.hmcts.dts.fact.controllers.admin;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
import uk.gov.hmcts.dts.fact.model.admin.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtApplicationUpdateService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

/**
 * Controller for retrieving and updating court application progressions.
 */
@RateLimiter(name = "default")
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtApplicationUpdateController {
    private final AdminCourtApplicationUpdateService adminCourtApplicationUpdateService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Construct a new AdminCourtApplicationUpdateController.
     * @param adminService the admin court application update service
     * @param adminCourtLockService the admin court lock service
     */
    @Autowired
    public AdminCourtApplicationUpdateController(AdminCourtApplicationUpdateService adminService,
                                                 AdminCourtLockService adminCourtLockService) {
        this.adminCourtApplicationUpdateService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Retrieves application progression methods for a specific service centre.
     * @param slug Court slug
     * @return A list of the service centre's application progressions
     */
    @GetMapping(path = "/{slug}/application-progression")
    @Operation(summary = "Find application progression options by slug")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<ApplicationUpdate>> getApplicationUpdates(@PathVariable String slug) {
        return ok(adminCourtApplicationUpdateService.getApplicationUpdatesBySlug(slug));
    }

    /**
     * Replace the existing application progressions with the new ones for a specific service centre.
     * @param slug Court slug
     * @param adminApplicationUpdates a list of the service centre's application progression methods to be updated
     * @return A list of updated application progressions
     */
    @PutMapping(path = "/{slug}/application-progression")
    @Operation(summary = "Update application progression options for a provided service centre")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<ApplicationUpdate>> updateApplicationUpdates(@PathVariable String slug,
                                                                            @RequestBody List<ApplicationUpdate> adminApplicationUpdates,
                                                                            Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminCourtApplicationUpdateService.updateApplicationUpdates(slug, adminApplicationUpdates));
    }
}
