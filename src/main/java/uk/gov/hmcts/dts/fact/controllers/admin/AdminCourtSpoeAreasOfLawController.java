package uk.gov.hmcts.dts.fact.controllers.admin;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import uk.gov.hmcts.dts.fact.model.admin.SpoeAreaOfLaw;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtSpoeAreasOfLawService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

/**
 * Controller for updating court single point of entry areas of law.
 */
@RateLimiter(name = "default")
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtSpoeAreasOfLawController {
    private final AdminCourtSpoeAreasOfLawService adminCourtAreasOfLawSpoeService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Constructor for the AdminCourtSpoeAreasOfLawController.
     */
    @Autowired
    public AdminCourtSpoeAreasOfLawController(AdminCourtSpoeAreasOfLawService adminService,
                                              AdminCourtLockService adminCourtLockService) {
        this.adminCourtAreasOfLawSpoeService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Get all spoe areas of law.
     *
     * @return all spoe areas of law
     */
    @GetMapping(path = "/SpoeAreasOfLaw")
    @Operation(summary = "Return all spoe areas of law")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<List<SpoeAreaOfLaw>> getAllAreasOfLaw() {
        return ok(adminCourtAreasOfLawSpoeService.getAllSpoeAreasOfLaw());
    }

    /**
     * Get spoe areas of law by slug.
     *
     * @param slug the slug of the court
     * @return the spoe areas of law for the court
     */
    @GetMapping(path = "/{slug}/SpoeAreasOfLaw")
    @Operation(summary = "Find the spoe areas of law for a court")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<List<SpoeAreaOfLaw>> getCourtAreasOfLaw(@PathVariable String slug) {
        return ok(adminCourtAreasOfLawSpoeService.getCourtSpoeAreasOfLawBySlug(slug));
    }

    /**
     * Update spoe areas of law for a court.
     *
     * @param slug      the slug of the court
     * @param areasOfLaw the spoe areas of law for the court
     * @return the updated spoe areas of law for the court
     */
    @PutMapping(path = "/{slug}/SpoeAreasOfLaw")
    @Operation(summary = "Update the spoe areas of law for a court")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @ApiResponse(responseCode = "409", description = "Duplicate single point of entries exist")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<List<SpoeAreaOfLaw>> updateCourtAreasOfLaw(@PathVariable String slug,
                                                                     @RequestBody List<SpoeAreaOfLaw> areasOfLaw,
                                                                     Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminCourtAreasOfLawSpoeService.updateSpoeAreasOfLawForCourt(slug, areasOfLaw));
    }
}
