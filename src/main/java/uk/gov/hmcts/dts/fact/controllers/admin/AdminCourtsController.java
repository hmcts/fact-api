package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.services.admin.AdminService;

import java.util.List;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;


@RestController
@RequestMapping(
    path = "/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtsController {

    private final AdminService adminService;

    @Autowired
    public AdminCourtsController(final AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/all")
    @ApiOperation("Return all courts")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtReference>> getAllCourts() {
        return ok(adminService.getAllCourtReferences());
    }

    @GetMapping(path = "/")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    @ApiOperation("Return court data for download")
    public ResponseEntity<List<uk.gov.hmcts.dts.fact.model.CourtForDownload>> getAllCourtsForDownload() {
        return ok(adminService.getAllCourtsForDownload());
    }

    @PutMapping(path = "/info")
    @ApiOperation("Update selected courts info")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Void> updateCourtsInfo(@RequestBody CourtInfoUpdate info) {
        adminService.updateMultipleCourtsInfo(info);

        return noContent().build();
    }

    @GetMapping(path = "/{slug}/general")
    @ApiOperation("Find court details by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<Court> findCourtByName(@PathVariable String slug) {
        return ok(adminService.getCourtBySlug(slug));
    }

    @PutMapping(path = "/{slug}/general")
    @ApiOperation("Update court")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<Court> updateCourtBySlug(@PathVariable String slug, @RequestBody Court updatedCourt) {
        return ok(adminService.save(slug, updatedCourt));
    }
}
