package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtEmailService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@Slf4j
public class AdminCourtEmailController {
    private final AdminCourtEmailService adminCourtEmailService;

    @Autowired
    public AdminCourtEmailController(AdminCourtEmailService adminService) {
        this.adminCourtEmailService = adminService;
    }

    @GetMapping(path = "/{slug}/emails")
    @ApiOperation("Find email addresses by slug")
//    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Email>> getCourtEmails(@PathVariable String slug) {
        return ok(adminCourtEmailService.getCourtEmailsBySlug(slug));
    }

    @PostMapping(path = "/{slug}/emails/test")
    @ApiOperation("Update email addresses for a provided court")
//    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Email>> updateCourtEmails(@PathVariable String slug,
                                                               @RequestBody List<Email> adminEmails) {
        log.info("Goes into main bit");
        return ok(adminCourtEmailService.updateEmailListForCourt(slug, adminEmails));
    }

    @GetMapping(path = "/emails")
    @ApiOperation("Retrieve all email details for provided court")
//    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<EmailType>> getAllCourtEmailDescTypes() {
        return ok(adminCourtEmailService.getAllEmailTypes());
    }
}
