package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtApplicationUpdateService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtApplicationUpdateController {
    private final AdminCourtApplicationUpdateService adminCourtApplicationUpdateService;

    @Autowired
    public AdminCourtApplicationUpdateController(AdminCourtApplicationUpdateService adminService){
        this.adminCourtApplicationUpdateService = adminService;
    }

    @GetMapping(path = "/{slug}/application-progression")
    @ApiOperation("Find application progression options by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<ApplicationUpdate>> getApplicationUpdates(@PathVariable String slug){
        System.out.println(adminCourtApplicationUpdateService.getApplicationUpdatesBySlug(slug));
        return ok(adminCourtApplicationUpdateService.getApplicationUpdatesBySlug(slug));
    }

}
