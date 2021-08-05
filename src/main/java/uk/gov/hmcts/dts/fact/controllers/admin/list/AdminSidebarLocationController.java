package uk.gov.hmcts.dts.fact.controllers.admin.list;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.SidebarLocation;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminSidebarLocationService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/sidebarLocations",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminSidebarLocationController {
    private final AdminSidebarLocationService adminService;

    @Autowired
    public AdminSidebarLocationController(AdminSidebarLocationService adminService) {
        this.adminService = adminService;
    }

    /**
     * Retrieves all sidebar locations.
     * @return A list of sidebar locations
     */
    @GetMapping()
    @ApiOperation("Retrieve all sidebar locations")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = SidebarLocation.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<SidebarLocation>> getAllSidebarLocations() {
        return ok(adminService.getAllSidebarLocations());
    }
}
