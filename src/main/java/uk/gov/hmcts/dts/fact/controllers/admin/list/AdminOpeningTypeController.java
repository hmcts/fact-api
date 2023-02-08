package uk.gov.hmcts.dts.fact.controllers.admin.list;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation("Retrieve all opening types")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = OpeningType.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<OpeningType>> getAllOpeningTypes() {
        return ok(adminService.getAllOpeningTypes());
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Get opening type")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = OpeningType.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Opening type not found")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<OpeningType> getOpeningType(@PathVariable Integer id) {
        return ok(adminService.getOpeningType(id));
    }

    @PostMapping()
    @ApiOperation("Create opening type")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = OpeningType.class),
        @ApiResponse(code = 400, message = "Invalid opening type", response = String.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 409, message = "Opening type already exists")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<OpeningType> createOpeningType(@RequestBody OpeningType openingType) {
        return created(URI.create(StringUtils.EMPTY)).body(adminService.createOpeningType(openingType));
    }

    @PutMapping()
    @ApiOperation("Update opening type")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = OpeningType.class),
        @ApiResponse(code = 400, message = "Invalid Opening type", response = String.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Opening type not found")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<OpeningType> updateOpeningType(@RequestBody OpeningType openingType) {
        return ok(adminService.updateOpeningType(openingType));
    }

    @DeleteMapping("/{openingTypeId}")
    @ApiOperation("Delete opening type")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = OpeningType.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Opening type not found"),
        @ApiResponse(code = 409, message = "Opening type in use")
    })
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Integer> deleteOpeningType(@PathVariable Integer openingTypeId) {
        adminService.deleteOpeningType(openingTypeId);
        return ok().body(openingTypeId);
    }
}
