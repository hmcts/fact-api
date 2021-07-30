package uk.gov.hmcts.dts.fact.controllers.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.config.security.Role;
import uk.gov.hmcts.dts.fact.exception.InvalidPostcodeException;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtAddressService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@RestController
@RequestMapping(
    path = "/admin/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtAddressController {
    private final AdminCourtAddressService adminService;

    @Autowired
    public AdminCourtAddressController(AdminCourtAddressService adminService) {
        this.adminService = adminService;
    }

    /**
     * Retrieves addresses for a specific court.
     * @param slug Court slug
     * @return A list of court addresses
     */
    @GetMapping(path = "/{slug}/addresses")
    @ApiOperation("Find court addresses by slug")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = CourtAddress.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not Found")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtAddress>> getCourtAddresses(@PathVariable String slug) {
        return ok(adminService.getCourtAddressesBySlug(slug));
    }

    /**
     * Replace the existing addresses with the new ones for a specific court.
     * @param slug Court slug
     * @param courtAddresses a list of court addresses to be updated
     * @return A list of updated court addresses
     *         If one of more input address postcodes are invalid, return the invalid postcodes and a '400' response.
     */
    @PutMapping(path = "/{slug}/addresses")
    @ApiOperation("Update addresses for a provided court")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful", response = CourtAddress.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid postcodes", response = String.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Court not Found")
    })
    @Role({FACT_ADMIN, FACT_SUPER_ADMIN})
    public ResponseEntity<List<CourtAddress>> updateCourtAddresses(@PathVariable String slug, @RequestBody List<CourtAddress> courtAddresses) {
        final List<String> invalidPostcodes = adminService.validateCourtAddressPostcodes(slug, courtAddresses);
        if (CollectionUtils.isEmpty(invalidPostcodes)) {
            return ok(adminService.updateCourtAddresses(slug, courtAddresses));
        }
        throw new InvalidPostcodeException(invalidPostcodes);
    }
}
