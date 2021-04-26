package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtTypesService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.controllers.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.controllers.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtTypesController {
    private final AdminCourtTypesService courtTypesService;

    @Autowired
    public AdminCourtTypesController(AdminCourtTypesService adminService) {
        this.courtTypesService = adminService;
    }

    @GetMapping(path = "/courtTypes/all")
    @ApiOperation("Return all court types")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtType>> getAllCourtTypes() {
        return ok(courtTypesService.getAllCourtTypes());
    }

    @GetMapping(path = "/{slug}/courtTypes")
    @ApiOperation("Find a court's court types by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtType>> getCourtCourtTypes(@PathVariable String slug) {
        return ok(courtTypesService.getCourtCourtTypesBySlug(slug));
    }

    @PutMapping(path = "/{slug}/courtTypes")
    @ApiOperation("Update a court's court types")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtType>> updateCourtCourtTypes(@PathVariable String slug, @RequestBody List<CourtType> courtTypes) {
        return ok(courtTypesService.updateCourtCourtTypes(slug, courtTypes));
    }
}
