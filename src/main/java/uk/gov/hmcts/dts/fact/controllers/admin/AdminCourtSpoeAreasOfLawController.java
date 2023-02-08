package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtSpoeAreasOfLawController {
    private final AdminCourtSpoeAreasOfLawService adminCourtAreasOfLawSpoeService;
    private final AdminCourtLockService adminCourtLockService;

    @Autowired
    public AdminCourtSpoeAreasOfLawController(AdminCourtSpoeAreasOfLawService adminService,
                                              AdminCourtLockService adminCourtLockService) {
        this.adminCourtAreasOfLawSpoeService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    @GetMapping(path = "/SpoeAreasOfLaw")
    @ApiOperation("Return all spoe areas of law")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = SpoeAreaOfLaw.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<List<SpoeAreaOfLaw>> getAllAreasOfLaw() {
        return ok(adminCourtAreasOfLawSpoeService.getAllSpoeAreasOfLaw());
    }

    @GetMapping(path = "/{slug}/SpoeAreasOfLaw")
    @ApiOperation("Find the spoe areas of law for a court")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = SpoeAreaOfLaw.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not Found")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<List<SpoeAreaOfLaw>> getCourtAreasOfLaw(@PathVariable String slug) {
        return ok(adminCourtAreasOfLawSpoeService.getCourtSpoeAreasOfLawBySlug(slug));
    }

    @PutMapping(path = "/{slug}/SpoeAreasOfLaw")
    @ApiOperation("Update the spoe areas of law for a court")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = SpoeAreaOfLaw.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not Found"),
        @ApiResponse(code = 409, message = "Duplicate single point of entries exist")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<List<SpoeAreaOfLaw>> updateCourtAreasOfLaw(@PathVariable String slug,
                                                                     @RequestBody List<SpoeAreaOfLaw> areasOfLaw,
                                                                     Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminCourtAreasOfLawSpoeService.updateSpoeAreasOfLawForCourt(slug, areasOfLaw));
    }
}
