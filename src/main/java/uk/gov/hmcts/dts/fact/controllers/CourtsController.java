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
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;
import uk.gov.hmcts.dts.fact.services.CourtService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

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
     *
     * @deprecated Use {@link #findCourtByName}, path = /{slug}}
     */
    @Deprecated(since = "1.0", forRemoval = true)
    @GetMapping(path = "/{slug}.json")
    @Operation(summary = "Find court details by name")
    public ResponseEntity<OldCourt> findCourtByNameDeprecated(@PathVariable String slug) {
        return ok(courtService.getCourtBySlugDeprecated(slug));
    }

    @GetMapping
    @Operation(summary = "Find courts by name, address, town or postcode")
    public ResponseEntity<List<CourtReference>> findCourtByNameOrAddressOrPostcodeOrTown(@RequestParam(name = "q") String query) {
        if (query.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ok(courtService.getCourtByNameOrAddressOrPostcodeOrTownFuzzyMatch(query));
    }

    @GetMapping(path = "/{slug}")
    @Operation(summary = "Find court details by slug")
    public ResponseEntity<Court> findCourtByName(@PathVariable String slug) {
        return ok(courtService.getCourtBySlug(slug));
    }

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
}
