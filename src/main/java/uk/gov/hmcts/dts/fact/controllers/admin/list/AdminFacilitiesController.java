package uk.gov.hmcts.dts.fact.controllers.admin.list;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    @Operation(summary = "Return all facility types")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<FacilityType>> getAllFacilities() {
        return ok(adminFacilityService.getAllFacilityTypes());
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Get facility type")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Facility not found")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<FacilityType> getFacilityType(@PathVariable Integer id) {
        return ok(adminFacilityService.getFacilityType(id));
    }

    @PostMapping(path = "")
    @Operation(summary = "Create new facility type")
    @Role({FACT_SUPER_ADMIN})
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "409", description = "Facility Type already exists")
    public ResponseEntity<FacilityType> createFacilityType(@RequestBody FacilityType facilityType) {
        return created(URI.create(StringUtils.EMPTY)).body(adminFacilityService.createFacilityType(facilityType));
    }

    @PutMapping(path = "")
    @Operation(summary = "Update facility type")
    @Role({FACT_SUPER_ADMIN})
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Facility Type not found")
    @ApiResponse(responseCode = "409", description = "Facility Type already exists")
    public ResponseEntity<FacilityType> updateFacilityType(@RequestBody FacilityType facilityType) {
        return ok(adminFacilityService.updateFacilityType(facilityType));
    }

    @PutMapping(path = "/reorder")
    @Operation(summary = "Reorder facility types")
    @Role({FACT_SUPER_ADMIN})
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Facility Type not found")
    public ResponseEntity<List<FacilityType>> reorderFacilityTypes(@RequestBody List<Integer> orderedFacilityTypeIds) {
        return ok(adminFacilityService.reorderFacilityTypes(orderedFacilityTypeIds));
    }

    @DeleteMapping("/{facilityTypeId}")
    @Operation(summary = "Delete facility type")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Facility Type not found")
    @ApiResponse(responseCode = "409", description = "Facility Type in use")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Integer> deleteFacilityType(@PathVariable Integer facilityTypeId) {
        adminFacilityService.deleteFacilityType(facilityTypeId);
        return ok().body(facilityTypeId);
    }
}
