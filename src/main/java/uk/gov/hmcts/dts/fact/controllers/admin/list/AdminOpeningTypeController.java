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
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminOpeningTypeService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/openingTypes",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class AdminOpeningTypeController {

    private final AdminOpeningTypeService adminService;

    @Autowired
    public AdminOpeningTypeController(AdminOpeningTypeService adminService) {
        this.adminService = adminService;
    }


    @GetMapping()
    @Operation(summary = "Retrieve all opening types")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<OpeningType>> getAllOpeningTypes() {
        return ok(adminService.getAllOpeningTypes());
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Get opening type")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Opening type not found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<OpeningType> getOpeningType(@PathVariable Integer id) {
        return ok(adminService.getOpeningType(id));
    }

    @PostMapping()
    @Operation(summary = "Create opening type")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Invalid opening type")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "409", description = "Opening type already exists")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<OpeningType> createOpeningType(@RequestBody OpeningType openingType) {
        return created(URI.create(StringUtils.EMPTY)).body(adminService.createOpeningType(openingType));
    }

    @PutMapping()
    @Operation(summary = "Update opening type")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "400", description = "Invalid Opening type")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Opening type not found")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<OpeningType> updateOpeningType(@RequestBody OpeningType openingType) {
        return ok(adminService.updateOpeningType(openingType));
    }

    @DeleteMapping("/{openingTypeId}")
    @Operation(summary = "Delete opening type")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Opening type not found")
    @ApiResponse(responseCode = "409", description = "Opening type in use")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Integer> deleteOpeningType(@PathVariable Integer openingTypeId) {
        adminService.deleteOpeningType(openingTypeId);
        return ok().body(openingTypeId);
    }
}
