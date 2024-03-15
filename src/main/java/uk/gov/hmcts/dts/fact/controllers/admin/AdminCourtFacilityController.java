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
import uk.gov.hmcts.dts.fact.model.admin.Facility;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtFacilityService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

/**
 * Controller for retrieving and updating court facilities.
 */
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtFacilityController {
    private final AdminCourtFacilityService adminCourtFacilityService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Construct a new AdminCourtFacilityController.
     * @param adminService the admin court facility service
     * @param adminCourtLockService the admin court lock service
     */
    @Autowired
    public AdminCourtFacilityController(AdminCourtFacilityService adminService,
                                        AdminCourtLockService adminCourtLockService) {
        this.adminCourtFacilityService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Retrieves facilities for a specific court.
     * @param slug Court slug
     * @return A list of court facilities
     */
    @GetMapping(path = "/{slug}/facilities")
    @Operation(summary = "Find a court's facilities by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not found")
    public ResponseEntity<List<Facility>> getCourtFacilities(@PathVariable String slug) {
        return ok(adminCourtFacilityService.getCourtFacilitiesBySlug(slug));
    }

    /**
     * Update facilities for a specific court.
     * @param slug Court slug
     * @param courtFacilities A list of court facilities
     * @param authentication Authentication object
     * @return A list of court facilities
     */
    @PutMapping(path = "/{slug}/facilities")
    @Operation(summary = "Update a court's facilities")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not found")
    public ResponseEntity<List<Facility>> updateCourtFacility(@PathVariable String slug,
                                                              @RequestBody List<Facility> courtFacilities,
                                                              Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminCourtFacilityService.updateCourtFacility(slug, courtFacilities));
    }
}

