package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtPostcodeService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtPostcodeController {
    private final AdminCourtPostcodeService adminService;

    @Autowired
    public AdminCourtPostcodeController(AdminCourtPostcodeService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/{slug}/postcodes")
    @ApiOperation("Find court postcodes by slug")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<String>> getCourtPostcodes(@PathVariable String slug) {
        return ok(adminService.getCourtPostcodesBySlug(slug));
    }

    @PutMapping(path = "/{slug}/postcodes")
    @ApiOperation("Update court postcodes")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<String>> updateCourtPostcodes(@PathVariable String slug, @RequestBody List<String> postcodes) {
        return ok(adminService.updateCourtPostcodes(slug, postcodes));
    }
}
