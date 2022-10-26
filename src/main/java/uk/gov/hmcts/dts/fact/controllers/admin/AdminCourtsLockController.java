package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.model.admin.ImageFile;
import uk.gov.hmcts.dts.fact.model.admin.NewCourt;
import uk.gov.hmcts.dts.fact.services.admin.AdminService;
import uk.gov.hmcts.dts.fact.util.Utils;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.*;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.*;

@Validated
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtsLockController {

    private final AdminService adminService;
//    private static final String FORBIDDEN = "Forbidden";
//    private static final String UNAUTHORISED = "Unauthorised";

    @Autowired
    public AdminCourtsLockController(final AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/{slug}/general")
    @ApiOperation("Find court details by slug")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<Court> findCourtByName(@PathVariable String slug) {
        return ok(adminService.getCourtBySlug(slug));
    }

//    @PostMapping()
//    @ApiOperation("Add a new court")
//    @ApiResponses(value = {
//        @ApiResponse(code = 201, message = "Created", response = Court.class),
//        @ApiResponse(code = 401, message = UNAUTHORISED),
//        @ApiResponse(code = 403, message = FORBIDDEN),
//        @ApiResponse(code = 409, message = "Court already exists")
//    })
//    @Role({FACT_SUPER_ADMIN})
//    public ResponseEntity<Court> addNewCourt(@Valid @RequestBody NewCourt newCourt) {
//        String newCourtSlug = Utils.convertNameToSlug(newCourt.getNewCourtName());
//        return created(URI.create("/courts/" + newCourtSlug + "/general"))
//            .body(adminService.addNewCourt(newCourt.getNewCourtName(),
//                                           newCourtSlug, newCourt.getServiceCentre(),
//                                           newCourt.getLon(), newCourt.getLat(),
//                                           newCourt.getServiceAreas()));
//    }
//
//    @DeleteMapping("/{slug}")
//    @ApiOperation("Delete a court")
//    @ApiResponses(value = {
//        @ApiResponse(code = 200, message = "Deleted"),
//        @ApiResponse(code = 401, message = UNAUTHORISED),
//        @ApiResponse(code = 403, message = FORBIDDEN)
//    })
//    @Role({FACT_SUPER_ADMIN})
//    public ResponseEntity deleteCourt(@PathVariable String slug) {
//        adminService.deleteCourt(slug);
//        return ok().body("Court with slug: " + slug + " has been deleted");
//    }
//
//    @PutMapping(path = "/{slug}/general")
//    @ApiOperation("Update court")
//    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
//    public ResponseEntity<Court> updateCourtBySlug(@PathVariable String slug, @RequestBody Court updatedCourt) {
//        return ok(adminService.save(slug, updatedCourt));
//    }
}
