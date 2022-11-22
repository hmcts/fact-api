package uk.gov.hmcts.dts.fact.controllers.admin.list;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.FacilityType;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminFacilityService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/facilities",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class AdminFacilitiesController {
    private final AdminFacilityService adminFacilityService;

    @Autowired
    public AdminFacilitiesController(AdminFacilityService adminFacilityService) {
        this.adminFacilityService = adminFacilityService;
    }

    @GetMapping(path = "")
    @ApiOperation("Return all facility types")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = FacilityType.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<FacilityType>> getAllFacilities() {
        return ok(adminFacilityService.getAllFacilityTypes());
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Get facility type")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = FacilityType.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Facility not found")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<FacilityType> getFacilityType(@PathVariable Integer id) {
        return ok(adminFacilityService.getFacilityType(id));
    }

    @PostMapping(path = "")
    @ApiOperation("Create new facility type")
    @Role({FACT_SUPER_ADMIN})
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = FacilityType.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 409, message = "Facility Type already exists")
    })
    public ResponseEntity<FacilityType> createFacilityType(@RequestBody FacilityType facilityType) {
        return created(URI.create(StringUtils.EMPTY)).body(adminFacilityService.createFacilityType(facilityType));
    }

    @PutMapping(path = "")
    @ApiOperation("Update facility type")
    @Role({FACT_SUPER_ADMIN})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = FacilityType.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Facility Type not found"),
        @ApiResponse(code = 409, message = "Facility Type already exists")
    })
    public ResponseEntity<FacilityType> updateFacilityType(@RequestBody FacilityType facilityType) {
        return ok(adminFacilityService.updateFacilityType(facilityType));
    }

    @PutMapping(path = "/reorder")
    @ApiOperation("Reorder facility types")
    @Role({FACT_SUPER_ADMIN})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = FacilityType.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Facility Type not found")
    })
    public ResponseEntity<List<FacilityType>> reorderFacilityTypes(@RequestBody List<Integer> orderedFacilityTypeIds) {
        return ok(adminFacilityService.reorderFacilityTypes(orderedFacilityTypeIds));
    }

    @DeleteMapping("/{facilityTypeId}")
    @ApiOperation("Delete facility type")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Facility Type not found"),
        @ApiResponse(code = 409, message = "Facility Type in use")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Integer> deleteFacilityType(@PathVariable Integer facilityTypeId) {
        adminFacilityService.deleteFacilityType(facilityTypeId);
        return ok().body(facilityTypeId);
    }
}
