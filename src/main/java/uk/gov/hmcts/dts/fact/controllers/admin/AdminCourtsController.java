package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfo;
import uk.gov.hmcts.dts.fact.services.AdminService;

import java.util.List;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping(
    path = "/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtsController {

    public static final String FACT_SUPER_ADMIN = "fact-super-admin";
    public static final String FACT_ADMIN = "fact-admin";

    private final AdminService adminService;

    @Autowired
    public AdminCourtsController(final AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/all")
    @ApiOperation("Return all courts")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtReference>> getAllCourts() {
        return ok(adminService.getAllCourts());
    }

    @PutMapping(path = "/all/info")
    @ApiOperation("Update all courts info")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Void> updateAllCourts(@RequestBody CourtInfo info) {
        adminService.updateAllCourts(info);

        return noContent().build();
    }

    @GetMapping(path = "/{slug}/general")
    @ApiOperation("Find court details by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtGeneral> findCourtGeneralByName(@PathVariable String slug) {
        return ok(adminService.getCourtGeneralBySlug(slug));
    }

    @PutMapping(path = "/{slug}/general")
    @ApiOperation("Update court")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<CourtGeneral> updateGeneralCourtBySlug(@PathVariable String slug, @RequestBody CourtGeneral updatedCourt) {
        return ok(adminService.saveGeneral(slug, updatedCourt));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String slugNotFoundHandler(NotFoundException ex) {
        return ex.getMessage();
    }
}
