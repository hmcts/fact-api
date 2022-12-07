package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtEmailService;
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
public class AdminCourtEmailController {
    private final AdminCourtEmailService adminCourtEmailService;
    private final AdminCourtLockService adminCourtLockService;

    @Autowired
    public AdminCourtEmailController(AdminCourtEmailService adminService,
                                     AdminCourtLockService adminCourtLockService) {
        this.adminCourtEmailService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    @GetMapping(path = "/{slug}/emails")
    @ApiOperation("Find email addresses by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Email>> getCourtEmails(@PathVariable String slug) {
        return ok(adminCourtEmailService.getCourtEmailsBySlug(slug));
    }

    @PutMapping(path = "/{slug}/emails")
    @ApiOperation("Update email addresses for a provided court")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Email>> updateCourtEmails(@PathVariable String slug,
                                                         @RequestBody List<Email> adminEmails,
                                                         Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminCourtEmailService.updateEmailListForCourt(slug, adminEmails));
    }

    @GetMapping(path = "/emailTypes")
    @ApiOperation("Retrieve all email details for provided court")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<EmailType>> getAllCourtEmailDescTypes() {
        return ok(adminCourtEmailService.getAllEmailTypes());
    }
}
