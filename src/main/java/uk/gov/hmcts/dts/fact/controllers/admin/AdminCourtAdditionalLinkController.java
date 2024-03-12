package uk.gov.hmcts.dts.fact.controllers.admin;

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
import uk.gov.hmcts.dts.fact.model.admin.AdditionalLink;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAdditionalLinkService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
/**
 * Controller for retrieving and updating court additional links.
 */
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtAdditionalLinkController {
    private final AdminCourtAdditionalLinkService adminService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Construct a new AdminCourtAdditionalLinkController.
     * @param adminService the admin court additional link service
     * @param adminCourtLockService the admin court lock service
     */
    @Autowired
    public AdminCourtAdditionalLinkController(AdminCourtAdditionalLinkService adminService,
                                              AdminCourtLockService adminCourtLockService) {
        this.adminService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Retrieve all additional links for a court.
     * @param slug the court slug
     * @return list of additional links
     */
    @GetMapping(path = "/{slug}/additionalLinks")
    @Operation(summary = "Find court additional links by slug")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<AdditionalLink>> getCourtAdditionalLinks(@PathVariable String slug) {
        return ok(adminService.getCourtAdditionalLinksBySlug(slug));
    }

    /**
     * Update additional links for a court.
     * @param slug the court slug
     * @param additionalLinks the additional links to update
     * @param authentication the authentication object
     * @return list of additional links
     */
    @PutMapping(path = "/{slug}/additionalLinks")
    @Operation(summary = "Update court additional links")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<AdditionalLink>> updateCourtAdditionalLinks(@PathVariable String slug,
                                                                           @RequestBody List<AdditionalLink> additionalLinks,
                                                                           Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminService.updateCourtAdditionalLinks(slug, additionalLinks));
    }
}
