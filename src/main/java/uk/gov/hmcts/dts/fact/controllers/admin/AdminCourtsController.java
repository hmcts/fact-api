package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.model.admin.ImageFile;
import uk.gov.hmcts.dts.fact.model.admin.NewCourt;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.services.admin.AdminService;
import uk.gov.hmcts.dts.fact.util.Utils;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_VIEWER;

@Validated
@RestController
@RequestMapping(
    path = "/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtsController {

    private final AdminService adminService;
    private final AdminCourtLockService adminCourtLockService;
    private static final String FORBIDDEN = "Forbidden";
    private static final String FORBIDDEN_CODE = "403";
    private static final String UNAUTHORISED = "Unauthorised";
    private static final String UNAUTHORISED_CODE = "401";

    @Autowired
    public AdminCourtsController(final AdminService adminService,
                                 AdminCourtLockService adminCourtLockService) {
        this.adminService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    @GetMapping(path = "/all")
    @Operation(summary = "Return all courts")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtReference>> getAllCourts() {
        return ok(adminService.getAllCourtReferences());
    }

    @GetMapping(path = "/")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    @Operation(summary = "Return court data for download")
    public ResponseEntity<List<uk.gov.hmcts.dts.fact.model.CourtForDownload>> getAllCourtsForDownload() {
        return ok(adminService.getAllCourtsForDownload());
    }

    @PutMapping(path = "/info")
    @Operation(summary = "Update selected courts info")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Void> updateCourtsInfo(@RequestBody CourtInfoUpdate info) {
        adminService.updateMultipleCourtsInfo(info);
        return noContent().build();
    }

    @GetMapping(path = "/{slug}/general")
    @Operation(summary = "Find court details by slug")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<Court> findCourtByName(@PathVariable String slug) {
        return ok(adminService.getCourtBySlug(slug));
    }

    @PostMapping()
    @Operation(summary = "Add a new court")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED)
    @ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN)
    @ApiResponse(responseCode = "409", description = "Court already exists")
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<Court> addNewCourt(@Valid @RequestBody NewCourt newCourt) {
        String newCourtSlug = Utils.convertNameToSlug(newCourt.getNewCourtName());
        return created(URI.create("/courts/" + newCourtSlug + "/general"))
            .body(adminService.addNewCourt(newCourt.getNewCourtName(),
                                           newCourtSlug, newCourt.getServiceCentre(),
                                           newCourt.getLon(), newCourt.getLat(),
                                           newCourt.getServiceAreas()
            ));
    }

    @DeleteMapping("/{slug}")
    @Operation(summary = "Delete a court")
    @ApiResponse(responseCode = "200", description = "Deleted")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED)
    @ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN)
    @Role({FACT_SUPER_ADMIN})
    public ResponseEntity<String> deleteCourt(@PathVariable String slug) {
        adminService.deleteCourt(slug);
        return ok().body("Court with slug: " + slug + " has been deleted");
    }

    @PutMapping(path = "/{slug}/general")
    @Operation(summary = "Update court")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<Court> updateCourtBySlug(@PathVariable String slug,
                                                   @RequestBody Court updatedCourt,
                                                   Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminService.save(slug, updatedCourt));
    }

    @GetMapping(path = "/{slug}/courtPhoto")
    @Operation(summary = "Find the photo for a court")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED)
    @ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN)
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<String> getCourtImageBySlug(@PathVariable String slug) {
        return ok(adminService.getCourtImage(slug));
    }

    @PutMapping(path = "/{slug}/courtPhoto")
    @Operation(summary = "Update the photo for a court")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED)
    @ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN)
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<String> updateCourtImageBySlug(@PathVariable String slug,
                                                         @RequestBody ImageFile imageFile,
                                                         Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminService.updateCourtImage(slug, imageFile.getImageName()));
    }
}
