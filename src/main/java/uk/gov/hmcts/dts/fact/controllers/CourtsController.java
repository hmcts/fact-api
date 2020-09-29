package uk.gov.hmcts.dts.fact.controllers;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.dts.fact.exception.SlugNotFoundException;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.services.CourtService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping(
    path = "/courts",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class CourtsController {

    @Autowired
    CourtService courtService;

    @Deprecated
    @GetMapping(path = "/{slug}.json")
    @ApiOperation("Find court details by name")
    public ResponseEntity<Court> findCourtByName(@PathVariable String slug) {
        return ok(courtService.getCourtBySlug(slug));
    }

    @GetMapping
    @ApiOperation("Find courts by name, address, town or postcode")
    public ResponseEntity<List<CourtReference>> findCourtByNameOrAddressOrPostcodeOrTown(@RequestParam String search) {
        return ok(courtService.getCourtByNameOrAddressOrPostcodeOrTown(search));
    }

    @ExceptionHandler(SlugNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String slugNotFoundHandler(SlugNotFoundException ex) {
        return ex.getMessage();
    }
}
