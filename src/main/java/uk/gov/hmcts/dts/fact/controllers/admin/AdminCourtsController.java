package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;
import uk.gov.hmcts.dts.fact.services.AdminService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping(
    path = "/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtsController {

    @Autowired
    AdminService adminService;

    @GetMapping(path = "/all")
    @ApiOperation("Return all courts")
    public ResponseEntity<List<CourtReference>> getAllCourts() {
        return ok(adminService.getAllCourts());
    }

    @GetMapping(path = "/{slug}/general")
    @ApiOperation("Find court details by slug")
    public ResponseEntity<CourtGeneral> findCourtGeneralByName(@PathVariable String slug) {
        return ok(adminService.getCourtGeneralBySlug(slug));
    }

    @PutMapping(path = "/{slug}/general")
    @ApiOperation("Update court")
    public ResponseEntity<CourtGeneral> updateGeneralCourtBySlug(@PathVariable String slug, @RequestBody CourtGeneral updatedCourt) {
        return ok(adminService.saveGeneral(slug, updatedCourt));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String slugNotFoundHandler(NotFoundException ex) {
        return ex.getMessage();
    }
}
