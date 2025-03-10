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
import uk.gov.hmcts.dts.fact.model.admin.Contact;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtContactService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

/**
 * Controller for retrieving and updating court contacts.
 */
@RateLimiter(name = "default")
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtContactController {
    private final AdminCourtContactService adminService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Construct a new AdminCourtContactController.
     * @param adminService the admin court contact service
     * @param adminCourtLockService the admin court lock service
     */
    @Autowired
    public AdminCourtContactController(AdminCourtContactService adminService,
                                       AdminCourtLockService adminCourtLockService) {
        this.adminService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Retrieves contacts for a specific court.
     * @param slug Court slug
     * @return A list of court contacts
     */
    @GetMapping(path = "/{slug}/contacts")
    @Operation(summary = "Find court contacts by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Contact>> getCourtContacts(@PathVariable String slug) {
        return ok(adminService.getCourtContactsBySlug(slug));
    }

    /**
     * Updates contacts for a specific court.
     * @param slug Court slug
     * @param contacts A list of court contacts
     * @param authentication The authentication object
     * @return A list of court contacts
     */
    @PutMapping(path = "/{slug}/contacts")
    @Operation(summary = "Update court contacts")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<Contact>> updateCourtContacts(@PathVariable String slug,
                                                             @RequestBody List<Contact> contacts,
                                                             Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminService.updateCourtContacts(slug, contacts));
    }
}
