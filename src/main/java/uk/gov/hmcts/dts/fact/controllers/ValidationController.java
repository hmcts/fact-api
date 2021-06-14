package uk.gov.hmcts.dts.fact.controllers;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.dts.fact.mapit.MapItService;

import static org.springframework.http.ResponseEntity.ok;

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
    @Description("Accepts an array of postcodes and determines if geographical information exists for each. If it " +
        "does not, the postcode is marked as not being valid. A response will indicate which postcode(s) need further " +
        "consideration by the end user and will be displayed on the admin portal.")
    public ResponseEntity<String[]> validatePostcodes(

        // TODO: trim spaces on array postcodes there and back

        // TODO: if we have a suffix in the outcode, followed by a letter, than accept as a partial, for example:
        // TODO: https://en.wikipedia.org/wiki/EC_postcode_area EC1A EC1M W1A W1C


        // TODO: before regex check - full
        // TODO: full postcode should have min of 5 characters max of 7
        // TODO:     - starting is 1 or 2 letters, 2 or 3 digits in the middle, always 2 letters at the very end
        // TODO:

        // TODO: before regex check - partial
        // TODO: make HP1a work as expected, make sure it does not go into search. ATM it will still pass
        // TODO: if we have 4 characters, in the format LLDL, then assume its something like EC1A or HP1A

        // TODO: before regex check - partial (2)
        // TODO: EC1AA should fail, not a valid postcode format
        // TODO:    - one or two letters at the start, two or three digits
        // TODO:        - If the starting is one letter:
        // TODO:            - sub part can be one number or two numbers or three numbers
        // TODO:            - or sub part can be one number, and one letter
        // TODO:            - or two numbers with one letter
        // TODO:            - or sub part can be one number, and one letter, and one number
        // TODO:            - or two numbers with one letter, and one number
        // TODO:        - if the starting is two letters:
        // TODO:            - sub part can be one number or two numbers or three numbers
        // TODO:            - or sub part can be one number, and one letter
        // TODO:            - or sub part can be one number, and one letter, and one number
        // TODO: Based on the above should fail EC1A1


        @RequestBody String[] postcodes) {
        return ok(mapItService.validatePostcodes(postcodes));
    }
}
