package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.Facility;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtFacilityService;

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

    @Autowired
    public AdminCourtFacilityController(AdminCourtFacilityService adminService) {
        this.adminCourtFacilityService = adminService;
    }

    @GetMapping(path = "/{slug}/facilities")
    @ApiOperation("Find a court's facilities by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Facility>> getCourtFacilities(@PathVariable String slug) {
        return ok(adminCourtFacilityService.getCourtFacilitiesBySlug(slug));
    }

    @PutMapping(path = "/{slug}/facilities")
    @ApiOperation("Update a court's facilities")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Facility>> updateCourtFacility(@PathVariable String slug, @RequestBody List<Facility> courtFacilities) {
        return ok(adminCourtFacilityService.updateCourtFacility(slug, courtFacilities));
    }
}

