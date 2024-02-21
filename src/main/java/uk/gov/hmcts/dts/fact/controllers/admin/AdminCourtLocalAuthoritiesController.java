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

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtLocalAuthoritiesController {
    private final AdminCourtLocalAuthoritiesService adminCourtLocalAuthoritiesService;
    private final AdminCourtLockService adminCourtLockService;

    @Autowired
    public AdminCourtLocalAuthoritiesController(AdminCourtLocalAuthoritiesService adminService,
                                                AdminCourtLockService adminCourtLockService) {
        this.adminCourtLocalAuthoritiesService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    //returns local authorities for a court by passing in a court slug ,area of law and returning local authorities list
    @GetMapping(path = "/{slug}/{areaOfLaw}/localAuthorities")
    @Operation(summary = "Find a courts local authorities by slug")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<LocalAuthority>> getCourtLocalAuthorities(@PathVariable String slug, @PathVariable String areaOfLaw) {
        return ok(adminCourtLocalAuthoritiesService.getCourtLocalAuthoritiesBySlugAndAreaOfLaw(slug, areaOfLaw));
    }

    //updates local authorities for a court by passing in a court slug ,area of law, local authorities list and returning updated local authorities
    // list by super admin user only
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
