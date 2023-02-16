package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.Region;
import uk.gov.hmcts.dts.fact.services.admin.AdminRegionService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.*;

@RestController
@RequestMapping(
    path = "/admin/regions",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminRegionController {
    private final AdminRegionService adminService;

    public AdminRegionController(AdminRegionService adminService) {
        this.adminService = adminService;
    }


    @GetMapping()
    @ApiOperation("Retrieve all regions")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = Region.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN, FACT_VIEWER})
    public ResponseEntity<List<Region>> getAllRegions() {
        return ok(adminService.getAllRegions());
    }
}
