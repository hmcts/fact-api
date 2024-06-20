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
import uk.gov.hmcts.dts.fact.model.admin.CourtHistory;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtHistoryService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@Validated
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtHistoryController {

    private final AdminCourtHistoryService adminCourtHistoryService;
    private final AdminCourtLockService adminCourtLockService;

    private static final String UNAUTHORISED_CODE = "401";
    private static final String SUCCESS_CODE = "200";
    private static final String NOT_FOUND_CODE = "404";

    private static final String UNAUTHORISED_USER = "Unauthorised user";
    private static final String PATH_SUFFIX = "/history";

    @Autowired
    public AdminCourtHistoryController(final AdminCourtHistoryService adminCourtHistoryService, AdminCourtLockService adminCourtLockService) {
        this.adminCourtHistoryService = adminCourtHistoryService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Get all court histories.
     * This endpoint can be used to get all the court histories that exist.
     * @path /history
     * @return {@link List} of {@link CourtHistory} a list of all the coyrt histories.
     */
    @GetMapping(PATH_SUFFIX)
    @Operation(summary = "Get all court histories")
    @ApiResponse(responseCode = SUCCESS_CODE, description = "Successful")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED_USER)
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<CourtHistory>> getAllCourtHistory() {
        return ok(adminCourtHistoryService.getAllCourtHistory());
    }

    /**
     * Get a court history by ID.
     * This endpoint can be used to get a specific court history with its court history ID.
     * @path /admin/courts/id/{courtHistoryId}/history
     * @input an ID
     * @return CourtHistory - a specific court history.
     */
    @GetMapping("id/{courtHistoryId}" + PATH_SUFFIX)
    @Operation(summary = "Get a court history")
    @ApiResponse(responseCode = SUCCESS_CODE, description = "Successful - Court History Found")
    @ApiResponse(responseCode = NOT_FOUND_CODE, description = "Court History Not Found")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED_USER)
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<CourtHistory> getCourtHistoryById(@PathVariable Integer courtHistoryId) {
        return ok(adminCourtHistoryService.getCourtHistoryById(courtHistoryId));
    }

    /**
     * Get a court history with a court ID.
     * This endpoint can be used to get all the court histories associated with a specific active court.
     * @input a court ID
     * @path /admin/courts/court-id/{courtId}/history
     * @return {@link List} of {@link CourtHistory} a list of a court's coyrt histories.
     */
    @GetMapping("/court-id/{courtId}" + PATH_SUFFIX)
    @Operation(summary = "Get all court histories of a court")
    @ApiResponse(responseCode = SUCCESS_CODE, description = "Successful - Will return an empty response when no Court matches ID")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED_USER)
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<CourtHistory>> getCourtHistoryByCourtId(@PathVariable Integer courtId) {
        return ok(adminCourtHistoryService.getCourtHistoryByCourtId(courtId));
    }

    /**
     * Get court histories with a court slug.
     * This endpoint can be used to get all the court histories associated with a specific court
     * using the court's slug.
     * @input a slug
     * @path /admin/courts/{slug}/history
     * @return {@link List} of {@link CourtHistory} a list of a court's coyrt histories.
     */
    @GetMapping("/{slug}" + PATH_SUFFIX)
    @Operation(summary = "Get all court histories of a court")
    @ApiResponse(responseCode = SUCCESS_CODE, description = "Successful - Will return an empty response when no Court matches ID")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED_USER)
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<CourtHistory>> getCourtHistoryByCourtSlug(@PathVariable String slug) {
        return ok(adminCourtHistoryService.getCourtHistoryByCourtSlug(slug));
    }

    /**
     * Get a court history by its court name.
     * This endpoint can be used to get all the court histories that exist.
     * @path /admin/courts/name/{courtName}/history
     * @return {@link List} of {@link CourtHistory} a list of all the coyrt histories that have that name.
     */
    @GetMapping("/name/{courtName}" + PATH_SUFFIX)
    @Operation(summary = "Get a court history using a historical court name")
    @ApiResponse(responseCode = SUCCESS_CODE, description = "Successful - Will return an empty response when no Court matches name")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED_USER)
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<CourtHistory>> getCourtHistoryByCourtName(@PathVariable String courtName) {
        return ok(adminCourtHistoryService.getCourtHistoryByCourtName(courtName));
    }

    /**
     * Add a court history.
     * This endpoint can be used to add a new court history.
     * @path /admin/courts/history
     * @body an existing court history
     * @return {@link List} of {@link CourtHistory} the court history created.
     */
    @PostMapping(PATH_SUFFIX)
    @Operation(summary = "Add a new court history")
    @ApiResponse(responseCode = "201", description = "Successfully created court")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED_USER)
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<CourtHistory> addCourtHistory(@Valid @RequestBody CourtHistory courtHistory) {
        return created(URI.create("/admin/courts/history"))
            .body(adminCourtHistoryService.addCourtHistory(courtHistory));
    }

    /**
     * Update a court history.
     * This endpoint can be used to update a single court history.
     * @path /admin/courts/history
     * @body a court history (search_court_id, court_name etc.)
     * @return CourtHistory the court history that was updated.
     */
    @PutMapping(PATH_SUFFIX)
    @Operation(summary = "Change an existing court history")
    @ApiResponse(responseCode = SUCCESS_CODE, description = "Successfully updated court")
    @ApiResponse(responseCode = NOT_FOUND_CODE, description = "Court History Not Found")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED_USER)
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<CourtHistory> updateCourtHistory(@Valid @RequestBody CourtHistory courtHistory) {
        return ok(adminCourtHistoryService.updateCourtHistory(courtHistory));
    }

    /**
     * Replaces the court histories of a court with the given ones.
     * This endpoint will delete all the court histories of a court and replace them with
     * the supplied histories
     * @path /admin/courts/{slug}/history
     * @param slug the slug of the court whose histories are to be changed
     * @param courtHistoryList a list of court histories that the court should now have
     * @return the updated list of court histories for the given court
     */
    @PutMapping("/{slug}" + PATH_SUFFIX)
    @Operation(summary = "Replace the court histories of a given court")
    @ApiResponse(responseCode = "201", description = "Successfully updated court")
    @ApiResponse(responseCode = NOT_FOUND_CODE, description = "Court Not Found")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED_USER)
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<CourtHistory>> updateCourtHistories(@PathVariable String slug, @Valid @RequestBody List<CourtHistory> courtHistoryList,
                                                                   Authentication authentication) {
        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminCourtHistoryService.updateCourtHistoriesBySlug(slug, courtHistoryList));
    }

    /**
     * Delete a court history by ID.
     * This endpoint can be used to delete a specific court history by its ID.
     * @path /admin/courts/id/{courtHistoryId}/history
     * @input a court history ID
     * @return CourtHistory the court history that was deleted.
     */
    @DeleteMapping("/id/{courtHistoryId}" + PATH_SUFFIX)
    @Operation(summary = "Delete a Court History")
    @ApiResponse(responseCode = SUCCESS_CODE, description = "Successfuly deleted court history")
    @ApiResponse(responseCode = NOT_FOUND_CODE, description = "Court History Not Found")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED_USER)
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<CourtHistory> deleteByCourtHistoryId(@PathVariable Integer courtHistoryId) {
        return ok(adminCourtHistoryService.deleteCourtHistoryById(courtHistoryId));
    }

    /**
     * Delete court histories by court ID.
     * This endpoint can be used to delete the court histories by a specific court.
     * @path /admin/courts/court-id/{courtId}/history
     * @input a court ID
     * @return {@link List} of {@link CourtHistory} a list of deleted court histories.
     */
    @DeleteMapping("/court-id/{courtId}" + PATH_SUFFIX)
    @ApiResponse(responseCode = SUCCESS_CODE, description = "Successfuly deleted court histories")
    @ApiResponse(responseCode = UNAUTHORISED_CODE, description = UNAUTHORISED_USER)
    @Operation(summary = "Delete the court histories of a specific court")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<CourtHistory>> deleteCourtHistoriesByCourtId(@PathVariable Integer courtId) {
        return ok(adminCourtHistoryService.deleteCourtHistoriesByCourtId(courtId));
    }
}
