package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtApplicationUpdateService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtApplicationUpdateController {
    private final AdminCourtApplicationUpdateService adminCourtApplicationUpdateService;

    @Autowired
    public AdminCourtApplicationUpdateController(AdminCourtApplicationUpdateService adminService) {
        this.adminCourtApplicationUpdateService = adminService;
    }

    /**
     * Retrieves application progression methods for a specific service centre.
     * @param slug Court slug
     * @return A list of the service centre's application progressions
     */
    @GetMapping(path = "/{slug}/application-progression")
    @ApiOperation("Find application progression options by slug")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = ApplicationUpdate.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not Found")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<ApplicationUpdate>> getApplicationUpdates(@PathVariable String slug) {
        return ok(adminCourtApplicationUpdateService.getApplicationUpdatesBySlug(slug));
    }

    /**
     * Replace the existing application progressions with the new ones for a specific service centre.
     * @param slug Court slug
     * @param adminApplicationUpdates a list of the service centre's application progression methods to be updated
     * @return A list of updated application progressions
     */
    @PutMapping(path = "/{slug}/application-progression")
    @ApiOperation("Update application progression options for a provided service centre")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = ApplicationUpdate.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not Found")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<ApplicationUpdate>> updateApplicationUpdates(@PathVariable String slug,
                                                                            @RequestBody List<ApplicationUpdate> adminApplicationUpdates) {
        return ok(adminCourtApplicationUpdateService.updateApplicationUpdates(slug, adminApplicationUpdates));
    }

}
