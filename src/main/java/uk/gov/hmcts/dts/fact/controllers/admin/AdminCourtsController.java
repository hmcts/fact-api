package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.AdminCourt;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;
import uk.gov.hmcts.dts.fact.services.AdminService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping(
    path = "/admin/courts",
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

    @GetMapping(path = "/{slug}")
    @ApiOperation("Find court details by slug")
    public ResponseEntity<AdminCourt> findCourtByName(@PathVariable String slug) {
        return ok(adminService.getCourtBySlug(slug));
    }

    @PutMapping(path = "/{slug}/general")
    @ApiOperation("Update court")
    public ResponseEntity<AdminCourt> updateGeneralCourtBySlug(@PathVariable String slug, @RequestBody CourtGeneral updatedCourt) {
        Court court = adminService.getCourtEntityBySlug(slug);
        court.setName(updatedCourt.getName());
        court.setNameCy(updatedCourt.getNameCy());
        court.setInfo(updatedCourt.getInfo());
        court.setInfoCy(updatedCourt.getInfoCy());
        court.setDisplayed(updatedCourt.getOpen());
        court.setAlert(updatedCourt.getAlert());
        court.setAlertCy(updatedCourt.getAlertCy());
        return ok(adminService.save(court));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String slugNotFoundHandler(NotFoundException ex) {
        return ex.getMessage();
    }
}
