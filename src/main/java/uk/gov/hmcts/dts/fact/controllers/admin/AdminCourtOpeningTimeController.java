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
import uk.gov.hmcts.dts.fact.model.admin.OpeningTime;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtOpeningTimeService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

/**
 * Controller for updating court opening times.
 */
@RateLimiter(name = "default")
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtOpeningTimeController {
    private final AdminCourtOpeningTimeService adminService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Constructor for the AdminCourtOpeningTimeController.
     */
    @Autowired
    public AdminCourtOpeningTimeController(AdminCourtOpeningTimeService adminService,
                                           AdminCourtLockService adminCourtLockService) {
        this.adminService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Get court opening times by slug.
     *
     * @param slug the slug of the court
     * @return the opening times for the court
     */
    @GetMapping(path = "/{slug}/openingTimes")
    @Operation(summary = "Find court opening times by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<OpeningTime>> getCourtOpeningTimes(@PathVariable String slug) {
        return ok(adminService.getCourtOpeningTimesBySlug(slug));
    }

    /**
     * Update court opening times.
     *
     * @param slug         the slug of the court
     * @param openingTimes the opening times for the court
     * @return the updated opening times for the court
     */
    @PutMapping(path = "/{slug}/openingTimes")
    @Operation(summary = "Update court opening times")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<OpeningTime>> updateCourtOpeningTimes(@PathVariable String slug,
                                                                     @RequestBody List<OpeningTime> openingTimes,
                                                                     Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminService.updateCourtOpeningTimes(slug, openingTimes));
    }

}
