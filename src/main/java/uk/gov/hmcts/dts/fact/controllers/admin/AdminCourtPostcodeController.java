package uk.gov.hmcts.dts.fact.controllers.admin;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.exception.InvalidPostcodeException;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtPostcodeService;
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_VIEWER;

/**
 * Controller for updating court postcodes.
 */
@RateLimiter(name = "default")
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class AdminCourtPostcodeController {
    private final AdminCourtPostcodeService adminService;
    private final ValidationService validationService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Constructor for the AdminCourtPostcodeController.
     */
    @Autowired
    public AdminCourtPostcodeController(AdminCourtPostcodeService adminService,
                                        ValidationService validationService,
                                        AdminCourtLockService adminCourtLockService) {
        this.adminService = adminService;
        this.validationService = validationService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Retrieves postcodes handled by the court for civil service type.
     *
     * @param slug Court slug
     * @return A list of postcodes
     */
    @GetMapping(path = "/{slug}/postcodes")
    @Operation(summary = "Find court postcodes by slug")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_VIEWER, FACT_SUPER_ADMIN})
    public ResponseEntity<List<String>> getCourtPostcodes(@PathVariable String slug) {
        return ok(adminService.getCourtPostcodesBySlug(slug));
    }

    /**
     * Adds new postcodes to be handled by the court for civil service type.
     *
     * @param slug      Court slug
     * @param postcodes a list of postcodes to be added
     * @return A list of postcodes created if successful.
     *      If one of more input postcodes are invalid, return the invalid postcodes and a '400' response.
     *      If one of more input postcodes already exist in the database, return the existed postcodes and a '409' response.
     */
    @PostMapping(path = "/{slug}/postcodes")
    @Operation(summary = "Add new court postcodes")
    @ApiResponse(responseCode = "201", description = "Postcodes created")
    @ApiResponse(responseCode = "400", description = "Invalid postcodes")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @ApiResponse(responseCode = "409", description = "Postcodes already exist")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<String>> addCourtPostcodes(@PathVariable String slug,
                                                          @RequestBody List<String> postcodes,
                                                          Authentication authentication) {
        final List<String> invalidPostcodes = validationService.validatePostcodes(postcodes);
        if (CollectionUtils.isEmpty(invalidPostcodes)) {
            adminService.checkPostcodesDoNotExist(slug, postcodes);
            adminCourtLockService.updateCourtLock(slug, authentication.getName());
            return created(URI.create(StringUtils.EMPTY)).body(adminService.addCourtPostcodes(slug, postcodes));
        }
        throw new InvalidPostcodeException(invalidPostcodes);
    }

    /**
     * Deletes existing postcodes currently handled by the court for civil service type.
     *
     * @param slug      Court slug
     * @param postcodes a list of postcodes to be deleted
     * @return The number of postcodes deleted if successful
     *      If one of more input postcodes are invalid, return the invalid postcodes and a '400' response.
     *      If one of more input postcodes do not exist in the database, return the not found postcodes and a '404' response.
     */
    @DeleteMapping(path = "/{slug}/postcodes")
    @Operation(summary = "Delete existing court postcodes")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "400", description = "Invalid postcodes")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Postcodes do not exist")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity deleteCourtPostcodes(@PathVariable String slug,
                                               @RequestBody List<String> postcodes,
                                               Authentication authentication) {
        final List<String> invalidPostcodes = validationService.validatePostcodes(postcodes);
        if (CollectionUtils.isEmpty(invalidPostcodes)) {
            adminService.checkPostcodesExist(slug, postcodes);
            adminCourtLockService.updateCourtLock(slug, authentication.getName());
            return ok(adminService.deleteCourtPostcodes(slug, postcodes));
        }
        throw new InvalidPostcodeException(invalidPostcodes);
    }

    /**
     * This endpoint moves a list of postcodes from one court to another.
     *
     * @param sourceSlug      The source slug is the court where the postcodes currently are
     * @param destinationSlug The slug of the court where the postcodes will be moved to
     * @param postcodes       a list of postcodes to be moved
     * @return A successful response if the courts have been moved from the source court to the destination court
     *      and also return a list of strings that have been updated.
     *      If one of more input postcodes are invalid, return the invalid postcodes and a '400' response.
     *      If one of more input postcodes do not exist in the source court, return the not found postcodes and a '404' response.
     *      If one of more input postcodes already exist in the destination court, return the conflicting postcodes and a '409' response.
     */
    @PutMapping(path = "/{sourceSlug}/{destinationSlug}/postcodes")
    @Operation(summary = "Move postcodes from one court to another")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "400", description = "Invalid postcodes")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Postcodes do not exist")
    @ApiResponse(responseCode = "409", description = "Postcodes already exist")
    @Role(FACT_SUPER_ADMIN)
    public ResponseEntity<List<String>> movePostcodes(@PathVariable String sourceSlug,
                                                      @PathVariable String destinationSlug,
                                                      @RequestBody List<String> postcodes) {
        final List<String> invalidPostcodes = validationService.validatePostcodes(postcodes);
        if (CollectionUtils.isEmpty(invalidPostcodes)) {
            return ok(adminService.moveCourtPostcodes(sourceSlug, destinationSlug, postcodes));
        }
        throw new InvalidPostcodeException(invalidPostcodes);
    }
}
