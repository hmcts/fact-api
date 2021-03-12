package uk.gov.hmcts.dts.fact.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.model.ServiceAreaWithCourtReferencesWithDistance;
import uk.gov.hmcts.dts.fact.model.deprecated.CourtWithDistance;
import uk.gov.hmcts.dts.fact.services.CourtService;

import javax.annotation.RegEx;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(
    path = "/search",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class SearchController {

    private final CourtService courtService;
    private final Pattern scottishPostcodeRegex = Pattern.compile("^(ZE|KW|IV|HS|PH|AB|DD|PA|FK|G[0-9]|KY|KA|DG|TD|EH|ML)", Pattern.CASE_INSENSITIVE);

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
    @ApiOperation("Find court by postcode, address or name")
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public ResponseEntity<List<CourtWithDistance>> findCourtByPostcode(
        @RequestParam Optional<String> postcode,
        @ApiParam("Area of Law") @RequestParam(name = "aol", required = false) Optional<String> areaOfLaw,
        @RequestParam(required = false, name = "q") Optional<String> query
    ) {
        if (postcode.isPresent() && areaOfLaw.isPresent()) {
            if(areaOfLaw.get().equals("Children") && scottishPostcodeRegex.matcher(postcode.get()).find()) {
                return ok(Collections.emptyList());
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
}
