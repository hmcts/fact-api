package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.CourtLocalAuthority;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLocalAuthoritiesService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtLocalAuthoritiesController {
    private final AdminCourtLocalAuthoritiesService adminCourtLocalAuthoritiesService;

    @Autowired
    public AdminCourtLocalAuthoritiesController(AdminCourtLocalAuthoritiesService adminService) {
        this.adminCourtLocalAuthoritiesService = adminService;
    }

    //returns all local authorities
    @GetMapping(path = "/localAuthorities")
    @ApiOperation("Return all local authorities")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<LocalAuthority>> getAllLocalAuthorities() {
        return ok(adminCourtLocalAuthoritiesService.getAllLocalAuthorities());
    }

    //returns local authorities for a court by passing in a court slug and returning local authorities list
    @GetMapping(path = "/{slug}/courtLocalAuthorities")
    @ApiOperation("Find a courts local authorities by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtLocalAuthority>> getCourtLocalAuthorities(@PathVariable String slug) {
        return ok(adminCourtLocalAuthoritiesService.getCourtLocalAuthoritiesBySlug(slug));
    }

    //updates local authorities for a court by passing in a court slug ,local authorities list and returning updated local authorities list
    @PutMapping(path = "/{slug}/courtLocalAuthorities")
    @ApiOperation("Update a courts local authorities")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtLocalAuthority>> updateCourtLocalAuthorities(@PathVariable String slug, @RequestBody List<CourtLocalAuthority> courtLocalAuthorities) {
        return ok(adminCourtLocalAuthoritiesService.updateCourtLocalAuthority(slug, courtLocalAuthorities));
    }
}
