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
import uk.gov.hmcts.dts.fact.model.admin.County;
import uk.gov.hmcts.dts.fact.services.admin.AdminCountyService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/counties",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCountyController {
    private final AdminCountyService adminService;

    public AdminCountyController(AdminCountyService adminService) {
        this.adminService = adminService;
    }


    @GetMapping()
    @ApiOperation("Retrieve all counties")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = County.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<County>> getAllCounties() {
        return ok(adminService.getAllCounties());
    }
}
