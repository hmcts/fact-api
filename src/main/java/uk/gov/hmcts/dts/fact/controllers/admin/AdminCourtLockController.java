package uk.gov.hmcts.dts.fact.controllers.admin;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.CourtLock;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_VIEWER;

/**
 * Controller for retrieving and updating court locks.
 */
@Validated
@RateLimiter(name = "default")
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtLockController {

    private final AdminCourtLockService adminCourtLockService;
    private static final String FORBIDDEN = "Forbidden";
    private static final String FORBIDDEN_CODE = "403";
    private static final String UNAUTHORISED = "Unauthorised";
    private static final String UNAUTHORISED_CODE = "401";

    /**
     * Construct a new AdminCourtLockController.
     * @param adminCourtLockService the admin court lock service
     */
    @Autowired
    public AdminCourtLockController(final AdminCourtLockService adminCourtLockService) {
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Retrieves court locks for a specific court.
     * @param slug Court slug
     * @return A list of court locks
     */
    @GetMapping(path = "/{slug}/lock")
    @Operation(summary = "Find court lock details by slug and username")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED)
    @ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN)
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtLock>> getCourtLocks(@PathVariable String slug) {
        return ok(adminCourtLockService.getCourtLocks(slug));
    }

    /**
     * Add a new lock on a court for a given user.
     * @param slug Court slug
     * @param courtLock The new court lock
     * @return The new court lock
     */
    @PostMapping("/{slug}/lock")
    @Operation(summary = "Add a new lock on a court for a given user")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED)
    @ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN)
    @ApiResponse(responseCode = "409", description = "Court lock already exists")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtLock> addNewCourtLock(@PathVariable String slug,
                                                     @Valid @RequestBody CourtLock courtLock) {
        return created(URI.create("/admin/courts/" + slug + "/lock"))
            .body(adminCourtLockService.addNewCourtLock(courtLock));
    }

    /**
     * Delete a court lock by slug and email.
     * @param slug Court slug
     * @param userEmail The user email
     * @return The updated list of court locks
     */
    @DeleteMapping("/{slug}/lock/{userEmail}")
    @Operation(summary = "Delete a court lock by slug and email")
    @ApiResponse(responseCode = "200", description = "Deleted")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED)
    @ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN)
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtLock>> deleteCourtLockBySlugAndEmail(@PathVariable String slug,
                                                                         @PathVariable String userEmail) {
        return ok().body(adminCourtLockService.deleteCourtLock(slug, userEmail));
    }

    /**
     * Delete a court lock by email.
     * @param userEmail The user email
     * @return The updated list of court locks
     */
    @DeleteMapping("/{userEmail}/lock")
    @Operation(summary = "Delete a court lock by email")
    @ApiResponse(responseCode = "200", description = "Delete a court by email")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED)
    @ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN)
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtLock>> deleteCourtByEmail(
        @PathVariable String userEmail) {
        return ok().body(adminCourtLockService.deleteCourtLockByEmail(userEmail));
    }
}
