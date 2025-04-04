package uk.gov.hmcts.dts.fact.controllers.admin.list;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.AddressType;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminAddressTypeService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

/**
 * Controller for retrieving address types.
 */
@RateLimiter(name = "default")
@RestController
@RequestMapping(
    path = "/admin/addressTypes",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminAddressTypeController {
    private final AdminAddressTypeService adminService;

    /**
     * Constructor for the address type controller.
     * @param adminService The service to retrieve address types
     */
    public AdminAddressTypeController(AdminAddressTypeService adminService) {
        this.adminService = adminService;
    }

    /**
     * Retrieve all address types.
     * @return List of address types
     */
    @GetMapping()
    @Operation(summary = "Retrieve all address types")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<AddressType>> getAllCourtAddressTypes() {
        return ok(adminService.getAllAddressTypes());
    }
}
