package uk.gov.hmcts.dts.fact.controllers;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.mapit.MapItService;

import javax.validation.constraints.NotEmpty;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin()
@RestController
@RequestMapping(
    path = "/validate",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class ValidationController {

    private final MapItService mapItService;

    @Autowired
    public ValidationController(MapItService mapItService) {
        this.mapItService = mapItService;
    }

    @PostMapping(path = "/postcodes")
    @ApiOperation("Validation of postcodes")
    @Description("Accepts an array of postcodes and determines if geographical information exists for each. If it "
        + "does not, the postcode is marked as not being valid. A response will indicate which postcode(s) need further "
        + "consideration by the end user and will be displayed on the admin portal.")
    public ResponseEntity<String[]> validatePostcodes(
        @RequestBody @NotEmpty String... postcodes) {
        return ok(mapItService.validatePostcodes(postcodes));
    }
}
