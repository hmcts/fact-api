package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtGeneralInfoService;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts/{slug}/generalInfo",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtGeneralInfoController {
    private final AdminCourtGeneralInfoService adminService;

    @Autowired
    public AdminCourtGeneralInfoController(AdminCourtGeneralInfoService adminService) {
        this.adminService = adminService;
    }

    @GetMapping()
    @ApiOperation("Retrieve court general information")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtGeneralInfo> getCourtGeneralInfo(@PathVariable String slug) {
        return ok(adminService.getCourtGeneralInfoBySlug(slug));
    }

    @PutMapping()
    @ApiOperation("Update court general information")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtGeneralInfo> updateCourtGeneralInfo(@PathVariable String slug, @RequestBody CourtGeneralInfo generalInfo) {
        return ok(adminService.updateCourtGeneralInfo(slug, generalInfo));
    }
}
