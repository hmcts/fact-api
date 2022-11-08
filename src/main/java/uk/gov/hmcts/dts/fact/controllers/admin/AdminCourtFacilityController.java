package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.Facility;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtFacilityService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtFacilityController {
    private final AdminCourtFacilityService adminCourtFacilityService;
    private final AdminCourtLockService adminCourtLockService;

    @Autowired
    public AdminCourtFacilityController(AdminCourtFacilityService adminService,
                                        AdminCourtLockService adminCourtLockService) {
        this.adminCourtFacilityService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    @GetMapping(path = "/{slug}/facilities")
    @ApiOperation("Find a court's facilities by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = Facility.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not found"),
    })
    public ResponseEntity<List<Facility>> getCourtFacilities(@PathVariable String slug) {
        return ok(adminCourtFacilityService.getCourtFacilitiesBySlug(slug));
    }

    @PutMapping(path = "/{slug}/facilities")
    @ApiOperation("Update a court's facilities")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = Facility.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not found"),
    })
    public ResponseEntity<List<Facility>> updateCourtFacility(@PathVariable String slug,
                                                              @RequestBody List<Facility> courtFacilities,
                                                              Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminCourtFacilityService.updateCourtFacility(slug, courtFacilities));
    }
}

