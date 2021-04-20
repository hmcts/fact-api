package uk.gov.hmcts.dts.fact.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.exception.InvalidPostcodeException;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.model.ServiceAreaWithCourtReferencesWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.CourtWithDistance;
import uk.gov.hmcts.dts.fact.services.CourtService;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Pattern;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping(
    path = "/search",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class SearchController {

    private final CourtService courtService;

    @Autowired
    public SearchController(final CourtService courtService) {
        this.courtService = courtService;
    }

    /**
     * Find court by postcode.
     * @deprecated Use {@link #findCourtsByPostcodeAndServiceArea}, path = /results}
     */
    @Deprecated(since = "1.0", forRemoval = true)
    @GetMapping(path = "/results.json")
    @ApiOperation("Find court by postcode, address or name")
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public ResponseEntity<List<CourtWithDistance>> findCourtByPostcode(
        @RequestParam Optional<String> postcode,
        @ApiParam("Area of Law") @RequestParam(name = "aol", required = false) Optional<String> areaOfLaw,
        @RequestParam(required = false, name = "q") Optional<String> query
    ) {
        if (postcode.isPresent() && areaOfLaw.isPresent()) {
            if (areaOfLaw.get().equals("Children")) {
                return ok(courtService.getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority(postcode.get(), areaOfLaw.get()));
            }
            return ok(courtService.getNearestCourtsByPostcodeAndAreaOfLaw(postcode.get(), areaOfLaw.get()));
        } else if (postcode.isPresent()) {
            return ok(courtService.getNearestCourtsByPostcode(postcode.get()));
        } else if (query.isPresent()) {
            return ok(courtService.getCourtsByNameOrAddressOrPostcodeOrTown(query.get()));
        } else {
            return badRequest().build();
        }
    }

    @GetMapping(path = "/results/{postcode}")
    @ApiOperation("Find closest courts by postcode")
    @Description("Endpoint to return the 10 closest courts for a provided postcode")
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public ResponseEntity<List<CourtReferenceWithDistance>> findCourtsByPostcode(
        @Pattern(regexp = "^[a-z]{1,2}\\d[a-z\\d]?\\s*\\d[a-z]{2}$",
            message = "Postcode does not match regex pattern")
        @PathVariable String postcode) {
        return ok(courtService.getNearestCourtReferencesByPostcode(postcode));
    }

    @GetMapping(path = "/results")
    @ApiOperation("Find courts by postcode and Service Area")
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public ResponseEntity<ServiceAreaWithCourtReferencesWithDistance> findCourtsByPostcodeAndServiceArea(
        @RequestParam Optional<String> postcode,
        @ApiParam("Service Area Slug") @RequestParam(name = "serviceArea") Optional<String> serviceAreaSlug
    ) {
        if (postcode.isPresent() && serviceAreaSlug.isPresent()) {
            return ok(courtService.getNearestCourtsByPostcodeSearch(postcode.get(), serviceAreaSlug.get()));
        } else {
            return badRequest().build();
        }
    }

    @ExceptionHandler(InvalidPostcodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String postcodeNotFoundHandler(InvalidPostcodeException ex) {
        return ex.getMessage();
    }
}
