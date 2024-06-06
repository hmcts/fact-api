package uk.gov.hmcts.dts.fact.controllers;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithHistoricalName;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.services.CourtService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Controller for retrieving courts.
 */
@RateLimiter(name = "default")
@RestController
@RequestMapping(
    path = "/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
@Validated
public class CourtsController {

    private final CourtService courtService;

    @Autowired
    public CourtsController(final CourtService courtService) {
        this.courtService = courtService;
    }

    /**
     * Find court by name.
     * @deprecated Use {@link #findCourtByName}, path = /{slug}}
     */
    @Deprecated(since = "1.0", forRemoval = true)
    @GetMapping(path = "/{slug}.json")
    @Operation(summary = "Find court details by name")
    public ResponseEntity<OldCourt> findCourtByNameDeprecated(@PathVariable String slug) {
        return ok(courtService.getCourtBySlugDeprecated(slug));
    }

    /**
     * Find courts by name, address, town or postcode.
     * @param query - name, address, town or postcode
     * @return array of courts that match address or partial address
     */
    @GetMapping
    @Operation(summary = "Find courts by name, address, town or postcode")
    public ResponseEntity<List<CourtReference>> findCourtByNameOrAddressOrPostcodeOrTown(@RequestParam(name = "q") String query) {
        if (query.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ok(courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch(query));
    }

    /**
     * Find court details by slug.
     * @param slug - slug of the court
     * @return Court details which matches given slug
     */
    @GetMapping(path = "/{slug}")
    @Operation(summary = "Find court details by slug")
    public ResponseEntity<Court> findCourtByName(@PathVariable String slug) {
        return ok(courtService.getCourtBySlug(slug));
    }

    /**
     * Return active courts based on a provided prefix.
     * @param prefix - prefix of the court name
     * @return array of courts that match the prefix
     */
    @GetMapping(path = "/search")
    @Operation(summary = "Return active courts based on a provided prefix")
    public ResponseEntity<List<CourtReference>> getCourtsBySearch(@RequestParam @Size(min = 1, max = 1) @NotBlank String prefix) {
        return ok(courtService.getCourtsByPrefixAndActiveSearch(prefix));
    }

    /**
     * Find courts by court types endpoint.
     * This endpoint can be used to search for courts that have a court type associated to it.
     * @input a comma seperated list of court types which can include any of (magistrates,family,crown,tribunal,county)
     * @return array of courts that contain any of the input court types.
     * @path /courts/court-types/{courtTypes}
     */
    @GetMapping(path = "/court-types/{courtTypes}")
    @Operation(summary = "Find courts by court types. This endpoint can be used to search for "
        + "courts that have a court type associated to it")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Schema(title = "Court types list", name = "CourtTypes", type = "List<String>", example = "magistrates,family,"
        + "crown,tribunal,county")
    public ResponseEntity<List<Court>> findByCourtTypes(@PathVariable List<String> courtTypes) {
        return ok(courtService.getCourtsByCourtTypes(courtTypes));
    }

    /**
     * Find Court by historical court name.
     * This endpoint can be used to search historical court names to get current court info. If the historical name exists, then the current court information e.g.
     * name, slug etc. are returned. The search ignores upper/lowercase but the words must match name exactly.
     * @input a search string
     * @return {@link CourtReferenceWithHistoricalName CourtReferenceWithHistoricalName.class} the current court info including the historical court name.
     * @path /court-history/search
     */
    @GetMapping(path = "/court-history/search")
    @ApiResponse(responseCode = "200", description = "Successful - returns empty CourtReferenceWithHistoricalName if no Court History Found")
    @ApiResponse(responseCode = "404", description = "Court History Found but No corresponding Court found")
    @Operation(summary = "Return active court based a search of old court names")
    public ResponseEntity<CourtReferenceWithHistoricalName> getCourtByCourtHistoryNameSearch(@RequestParam(name = "q") @NotBlank String query) {
        return ok(courtService.getCourtByCourtHistoryName(query));
    }
}
