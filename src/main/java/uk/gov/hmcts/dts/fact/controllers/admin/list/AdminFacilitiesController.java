package uk.gov.hmcts.dts.fact.controllers.admin.list;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.FacilityType;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminFacilityService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/facilities",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminFacilitiesController {
    private final AdminFacilityService adminFacilityService;

    @Autowired
    public AdminFacilitiesController(AdminFacilityService adminFacilityService) {
        this.adminFacilityService = adminFacilityService;
    }

    @GetMapping(path = "")
    @ApiOperation("Return all facility types")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<FacilityType>> getAllFacilityTypes() {
        return ok(adminFacilityService.getAllFacilityTypes());
    }

}
