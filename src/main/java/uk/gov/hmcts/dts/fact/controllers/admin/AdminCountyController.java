package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.County;
import uk.gov.hmcts.dts.fact.services.admin.AdminCountyService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
/**
 * Controller for retrieving all counties.
 */
@RestController
@RequestMapping(
    path = "/admin/counties",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCountyController {
    private final AdminCountyService adminService;

    /**
     * Construct a new AdminCountyController.
     * @param adminService the admin county service
     */
    public AdminCountyController(AdminCountyService adminService) {
        this.adminService = adminService;
    }

    /**
     * Retrieve all counties.
     * @return list of counties
     */
    @GetMapping()
    @Operation(summary = "Retrieve all counties")
    @ApiResponse(responseCode = "200", description = "Successful", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<County>> getAllCounties() {
        return ok(adminService.getAllCounties());
    }
}
