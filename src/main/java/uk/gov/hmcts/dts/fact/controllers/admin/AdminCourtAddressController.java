package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.exception.InvalidEpimIdException;
import uk.gov.hmcts.dts.fact.exception.InvalidPostcodeException;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAddressService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;
/**
 * Controller for retrieving and updating court addresses.
 */
@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtAddressController {
    private final AdminCourtAddressService adminService;
    private final AdminCourtLockService adminCourtLockService;

    /**
     * Construct a new AdminCourtAddressController.
     * @param adminService the admin court address service
     * @param adminCourtLockService the admin court lock service
     */
    @Autowired
    public AdminCourtAddressController(AdminCourtAddressService adminService,
                                       AdminCourtLockService adminCourtLockService) {
        this.adminService = adminService;
        this.adminCourtLockService = adminCourtLockService;
    }

    /**
     * Retrieves addresses for a specific court.
     * @param slug Court slug
     * @return A list of court addresses
     */
    @GetMapping(path = "/{slug}/addresses")
    @Operation(summary = "Find court addresses by slug")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtAddress>> getCourtAddresses(@PathVariable String slug) {
        return ok(adminService.getCourtAddressesBySlug(slug));
    }

    /**
     * Replace the existing addresses with the new ones for a specific court.
     * @param slug Court slug
     * @param courtAddresses a list of court addresses to be updated
     * @return A list of updated court addresses.
     * If one of more input address postcodes or epim ids are invalid, return them with a '400' response.
     */
    @PutMapping(path = "/{slug}/addresses")
    @Operation(summary = "Update addresses for a provided court")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "400", description = "Invalid postcodes or epim IDs")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Court not Found")
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtAddress>> updateCourtAddresses(@PathVariable String slug,
                                                                   @RequestBody List<CourtAddress> courtAddresses,
                                                                   Authentication authentication) {
        // Validate postcodes
        final List<String> invalidPostcodes = adminService.validateCourtAddressPostcodes(courtAddresses);
        if (!CollectionUtils.isEmpty(invalidPostcodes)) {
            throw new InvalidPostcodeException(invalidPostcodes);
        }

        // Validate epim IDs
        final List<String> invalidEpimIds = adminService.validateCourtAddressEpimIds(courtAddresses);
        if (!CollectionUtils.isEmpty(invalidEpimIds)) {
            throw new InvalidEpimIdException(invalidEpimIds);
        }

        adminCourtLockService.updateCourtLock(slug, authentication.getName());
        return ok(adminService.updateCourtAddressesAndCoordinates(slug, courtAddresses));
    }

}
