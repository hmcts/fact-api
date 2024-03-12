package uk.gov.hmcts.dts.fact.controllers;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.model.ServiceAreaWithCourtReferencesWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.CourtWithDistance;
import uk.gov.hmcts.dts.fact.services.CourtService;
import uk.gov.hmcts.dts.fact.util.Action;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RateLimiter(name = "default")
@RestController
@Validated
@RequestMapping(
    path = "/search",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class SearchController {

    private final CourtService courtService;
    private static final String CHILDRENAREAOFLAW = "Children";

    @Autowired
    public SearchController(final CourtService courtService) {
        this.courtService = courtService;
    }

    /**
     * Find court by postcode.
     *
     * @deprecated Use {@link #findCourtsByPostcodeAndServiceArea}, path = /results}
     */
    @Deprecated(since = "1.0", forRemoval = true)
    @GetMapping(path = "/results.json")
    @Operation(summary = "Find court by postcode, address or name")
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public ResponseEntity<List<CourtWithDistance>> findCourtByPostcode(
        @RequestParam Optional<String> postcode,
        @Parameter(ref = "Area of Law") @RequestParam(name = "aol", required = false) Optional<String> areaOfLaw,
        @RequestParam(required = false, name = "q") Optional<String> query
    ) {
        if (postcode.isPresent() && areaOfLaw.isPresent()) {
            if (CHILDRENAREAOFLAW.equals(areaOfLaw.get())) {
                return ok(courtService.getNearestCourtsByPostcodeAndAreaOfLawAndLocalAuthority(
                    postcode.get(),
                    areaOfLaw.get(),
                    true
                ));
            }
            return ok(courtService.getNearestCourtsByPostcodeAndAreaOfLaw(postcode.get(), areaOfLaw.get(), true));
        } else if (postcode.isPresent()) {
            return ok(courtService.getNearestCourtsByPostcode(postcode.get()));
        } else if (query.isPresent()) {
            return ok(courtService.getCourtsByNameOrAddressOrPostcodeOrTown(query.get(), true));
        } else {
            return badRequest().build();
        }
    }

    /**
     * Endpoint to return the 10 closest courts for a provided postcode
     * @param postcode
     * @return Array of 10 courts
     */
    @GetMapping(path = "/results/{postcode}")
    @Operation(summary = "Find closest courts by postcode")
    @Description("Endpoint to return the 10 closest courts for a provided postcode")
    public ResponseEntity<List<CourtReferenceWithDistance>> findCourtsByPostcode(
        @Pattern(regexp =
            "([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z]\\d{1,2})|(([A-Za-z]"
                + "[A-Ha-hJ-Yj-y]\\d{1,2})|(([A-Za-z]\\d[A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y]"
                + "\\d[A-Za-z]?))))\\s?\\d[A-Za-z]{2})",
            message = "Provided postcode is not valid")
        @PathVariable String postcode) {
        return ok(courtService.getNearestCourtReferencesByPostcode(postcode));
    }

    /**
     *
     * @param postcode
     * @param serviceAreaSlug
     * @param includeClosed
     * @param action
     * @return Array of courts by service area and postcode
     */
    @GetMapping(path = "/results")
    @Operation(summary = "Find courts by postcode and Service Area")
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public ResponseEntity<ServiceAreaWithCourtReferencesWithDistance> findCourtsByPostcodeAndServiceArea(
        @RequestParam Optional<String> postcode,
        @Parameter(ref = "Service Area Slug") @RequestParam(name = "serviceArea") Optional<String> serviceAreaSlug,
        @Parameter(ref = "Include Closed") @RequestParam(name = "includeClosed", required = false, defaultValue = "false") Boolean includeClosed,
        @RequestParam("action") Optional<Action> action
    ) {
        if (postcode.isPresent() && serviceAreaSlug.isPresent()) {
            if (action.isPresent() && Action.isNearest(action.get())) {
                return ok(courtService.getNearestCourtsByPostcodeActionAndAreaOfLawSearch(
                    postcode.get(),
                    serviceAreaSlug.get(),
                    Action.NEAREST,
                    includeClosed
                ));
            } else if ("childcare-arrangements".equals(serviceAreaSlug.get())) {
                return ok(courtService.getNearestCourtsByAreaOfLawSinglePointOfEntry(
                    postcode.get(),
                    serviceAreaSlug.get(),
                    CHILDRENAREAOFLAW,
                    Action.UNDEFINED,
                    includeClosed
                ));
            } else {
                return ok(courtService.getNearestCourtsByPostcodeSearch(
                    postcode.get(),
                    serviceAreaSlug.get(),
                    includeClosed,
                    Action.UNDEFINED
                ));
            }
        } else {
            return badRequest().build();
        }
    }
}
