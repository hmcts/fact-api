package uk.gov.hmcts.dts.fact.services.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ValidationService {

    private final PostcodeValidator mapitPostcodeValidator;

    @Autowired
    public ValidationService(PostcodeValidator mapitPostcodeValidator) {
        this.mapitPostcodeValidator = mapitPostcodeValidator;
    }

    /**
     * Accepts an array of strings and checks for each if mapit data exists.
     * @param postcodes An array of strings which are postcodes
     * @return An array of strings which indicate which postcodes have failed to return geographical information
     */
    public String[] validatePostcodes(String... postcodes) {
        return Arrays.stream(postcodes)
            .filter(postcode -> !mapitPostcodeValidator.postcodeDataExists(postcode.replaceAll("\\s+","")))
            .toArray(String[]::new);
    }
}
