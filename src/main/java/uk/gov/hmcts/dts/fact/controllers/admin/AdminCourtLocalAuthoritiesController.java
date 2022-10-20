package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLocalAuthoritiesService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.*;

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

    //returns local authorities for a court by passing in a court slug ,area of law and returning local authorities list
    @GetMapping(path = "/{slug}/{areaOfLaw}/localAuthorities")
    @ApiOperation("Find a courts local authorities by slug")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<LocalAuthority>> getCourtLocalAuthorities(@PathVariable String slug, @PathVariable String areaOfLaw) {
        return ok(adminCourtLocalAuthoritiesService.getCourtLocalAuthoritiesBySlugAndAreaOfLaw(slug, areaOfLaw));
    }

    //updates local authorities for a court by passing in a court slug ,area of law, local authorities list and returning updated local authorities
    // list by super admin user only
    @PutMapping(path = "/{slug}/{areaOfLaw}/localAuthorities")
    @ApiOperation("Update a courts local authorities for a area of law by super admin")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<List<LocalAuthority>> updateCourtLocalAuthorities(@PathVariable String slug, @PathVariable String areaOfLaw, @RequestBody List<LocalAuthority> localAuthorities) {
        return ok(adminCourtLocalAuthoritiesService.updateCourtLocalAuthority(slug, areaOfLaw, localAuthorities));
    }
}
