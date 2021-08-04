package uk.gov.hmcts.dts.fact.controllers.admin.list;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminAreasOfLawService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courtAreasOfLaw",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminAreasOfLawController {
    private final AdminAreasOfLawService adminAreasOfLawService;

    @Autowired
    public AdminAreasOfLawController(AdminAreasOfLawService adminAreasOfLawService) {
        this.adminAreasOfLawService = adminAreasOfLawService;
    }

    @GetMapping()
    @ApiOperation("Return all areas of law")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = AreaOfLaw.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<AreaOfLaw>> getAllAreasOfLaw() {
        return ok(adminAreasOfLawService.getAllAreasOfLaw());
    }
}

