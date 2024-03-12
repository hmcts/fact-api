package uk.gov.hmcts.dts.fact.controllers.admin;

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
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLocalAuthoritiesService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_VIEWER;

/**
 * Controller for retrieving and updating court local authorities.
 */
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtLocalAuthoritiesController {
    private final AdminCourtLocalAuthoritiesService adminCourtLocalAuthoritiesService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Construct a new AdminCourtLocalAuthoritiesController.
     * @param adminService the admin court local authorities service
     * @param adminCourtLockService the admin court lock service
     */
    @Autowired
    public AdminCourtLocalAuthoritiesController(AdminCourtLocalAuthoritiesService adminService,
                                                AdminCourtLockService adminCourtLockService) {
        this.adminCourtLocalAuthoritiesService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Retrieves local authorities for a specific court.
     * @param slug Court slug
     * @param areaOfLaw Area of law
     * @return A list of court local authorities
     */
    @GetMapping(path = "/{slug}/{areaOfLaw}/localAuthorities")
    @Operation(summary = "Find a courts local authorities by slug")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<LocalAuthority>> getCourtLocalAuthorities(@PathVariable String slug, @PathVariable String areaOfLaw) {
        return ok(adminCourtLocalAuthoritiesService.getCourtLocalAuthoritiesBySlugAndAreaOfLaw(slug, areaOfLaw));
    }

    /**
     * Updates local authorities for a specific court.
     * @param slug Court slug
     * @param areaOfLaw Area of law
     * @param localAuthorities The updated court local authorities
     */
    @PutMapping(path = "/{slug}/{areaOfLaw}/localAuthorities")
    @Operation(summary = "Update a courts local authorities for a area of law by super admin")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<List<LocalAuthority>> updateCourtLocalAuthorities(@PathVariable String slug,
                                                                            @PathVariable String areaOfLaw,
                                                                            @RequestBody List<LocalAuthority> localAuthorities,
                                                                            Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminCourtLocalAuthoritiesService.updateCourtLocalAuthority(slug, areaOfLaw, localAuthorities));
    }
}
