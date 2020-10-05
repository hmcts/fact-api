package uk.gov.hmcts.dts.fact.controllers.deprecated;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.model.Court2;
import uk.gov.hmcts.dts.fact.services.CourtService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(
    path = "/search/results.json",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class SearchController {

    @Autowired
    CourtService courtService;

    @Deprecated
    @GetMapping
    @ApiOperation("Find court by postcode, address or name")
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public ResponseEntity<List<Court2>> findCourtByPostcode(
        @RequestParam String postcode,
        @ApiParam("Area of Law") @RequestParam(name = "aol", required = false) String areaOfLaw,
        @ApiParam("Single Point of Entry") @RequestParam(required = false) String spoe,
        @RequestParam(required = false, name = "q") String name
    ) {
        return ok(courtService.getNearestCourts(postcode));
    }
}
