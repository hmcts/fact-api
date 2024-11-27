package uk.gov.hmcts.dts.fact.controllers.admin;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
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
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtEmailService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

/**
 * Controller for retrieving and updating court email addresses.
 */
@RateLimiter(name = "default")
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtEmailController {
    private final AdminCourtEmailService adminCourtEmailService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Construct a new AdminCourtEmailController.
     * @param adminService the admin court email service
     * @param adminCourtLockService the admin court lock service
     */
    @Autowired
    public AdminCourtEmailController(AdminCourtEmailService adminService,
                                     AdminCourtLockService adminCourtLockService) {
        this.adminCourtEmailService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Retrieves email addresses for a specific court.
     * @param slug Court slug
     * @return A list of court email addresses
     */
    @GetMapping(path = "/{slug}/emails")
    @Operation(summary = "Find email addresses by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Email>> getCourtEmails(@PathVariable String slug) {
        return ok(adminCourtEmailService.getCourtEmailsBySlug(slug));
    }

    /**
     * Update email addresses for a provided court.
     * @param slug Court slug
     * @param adminEmails A list of email addresses
     * @param authentication Authentication object
     * @return A list of updated email addresses
     */
    @PutMapping(path = "/{slug}/emails")
    @Operation(summary = "Update email addresses for a provided court")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Email>> updateCourtEmails(@PathVariable String slug,
                                                         @RequestBody List<Email> adminEmails,
                                                         Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminCourtEmailService.updateEmailListForCourt(slug, adminEmails));
    }

    /**
     * Retrieve all email details for provided court.
     * @return A list of email types
     */
    @GetMapping(path = "/emailTypes")
    @Operation(summary = "Retrieve all email details for provided court")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<EmailType>> getAllCourtEmailDescTypes() {
        return ok(adminCourtEmailService.getAllEmailTypes());
    }
}
