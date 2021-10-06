package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.model.admin.CourtTypesAndCodes;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtTypesAndCodesService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtTypesAndCodesController {
    private final AdminCourtTypesAndCodesService courtTypesAndCodesService;

    @Autowired
    public AdminCourtTypesAndCodesController(AdminCourtTypesAndCodesService adminService) {
        this.courtTypesAndCodesService = adminService;
    }

    @GetMapping(path = "/courtTypes")
    @ApiOperation("Return all court types")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = CourtType.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtType>> getAllCourtTypes() {
        return ok(courtTypesAndCodesService.getAllCourtTypes());
    }

    @GetMapping(path = "/{slug}/courtTypesAndCodes")
    @ApiOperation("Find a court's types, GBS code and Dx codes by slug")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = CourtTypesAndCodes.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not Found")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtTypesAndCodes> getCourtTypesAndCodes(@PathVariable String slug) {
        return ok(courtTypesAndCodesService.getCourtTypesAndCodes(slug));
    }

    @PutMapping(path = "/{slug}/courtTypesAndCodes")
    @ApiOperation("Update a court's types, GBS code and Dx codes")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = CourtTypesAndCodes.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not Found")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtTypesAndCodes> updateCourtTypesAndCodes(@PathVariable String slug, @RequestBody CourtTypesAndCodes courtTypesAndCodes) {
        return ok(courtTypesAndCodesService.updateCourtTypesAndCodes(slug, courtTypesAndCodes));
    }
}
