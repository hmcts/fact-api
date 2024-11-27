package uk.gov.hmcts.dts.fact.controllers.admin;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.Region;
import uk.gov.hmcts.dts.fact.services.admin.AdminRegionService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_VIEWER;

/**
 * Controller for updating region data.
 */
@RateLimiter(name = "relaxed")
@RestController
@RequestMapping(
    path = "/admin/regions",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminRegionController {
    private final AdminRegionService adminService;

    /**
     * Constructor for the AdminRegionController.
     */
    public AdminRegionController(AdminRegionService adminService) {
        this.adminService = adminService;
    }

    /**
     * Get all regions.
     *
     * @return all regions
     */
    @GetMapping()
    @Operation(summary = "Retrieve all regions")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN, FACT_VIEWER})
    public ResponseEntity<List<Region>> getAllRegions() {
        return ok(adminService.getAllRegions());
    }
}
