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
import uk.gov.hmcts.dts.fact.model.admin.AdditionalLink;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAdditionalLinkService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtAdditionalLinkController {
    private final AdminCourtAdditionalLinkService adminService;
    private final AdminCourtLockService adminCourtLockService;

    @Autowired
    public AdminCourtAdditionalLinkController(AdminCourtAdditionalLinkService adminService,
                                              AdminCourtLockService adminCourtLockService) {
        this.adminService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    @GetMapping(path = "/{slug}/additionalLinks")
    @ApiOperation("Find court additional links by slug")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = AdditionalLink.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not Found")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<AdditionalLink>> getCourtAdditionalLinks(@PathVariable String slug) {
        return ok(adminService.getCourtAdditionalLinksBySlug(slug));
    }

    @PutMapping(path = "/{slug}/additionalLinks")
    @ApiOperation("Update court additional links")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = AdditionalLink.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not Found")
    })
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<AdditionalLink>> updateCourtAdditionalLinks(@PathVariable String slug,
                                                                           @RequestBody List<AdditionalLink> additionalLinks,
                                                                           Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminService.updateCourtAdditionalLinks(slug, additionalLinks));
    }
}
