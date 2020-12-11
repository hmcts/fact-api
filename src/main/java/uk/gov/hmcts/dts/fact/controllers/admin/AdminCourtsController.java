package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
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

    @PutMapping(path = "/info")
    @ApiOperation("Update selected courts info")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Void> updateCourtsInfo(@RequestBody CourtInfoUpdate info) {
        adminService.updateMultipleCourtsInfo(info);

        return noContent().build();
    }

    @GetMapping(path = "/{slug}/general")
    @ApiOperation("Find court details by slug")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<Court> findCourtByName(@PathVariable String slug) {
        return ok(adminService.getCourtBySlug(slug));
    }

    @PutMapping(path = "/{slug}/general")
    @ApiOperation("Update court")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<Court> updateCourtBySlug(@PathVariable String slug, @RequestBody Court updatedCourt) {
        return ok(adminService.save(slug, updatedCourt));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String slugNotFoundHandler(NotFoundException ex) {
        return ex.getMessage();
    }
}
