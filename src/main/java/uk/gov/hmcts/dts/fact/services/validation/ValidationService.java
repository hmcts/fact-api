package uk.gov.hmcts.dts.fact.services.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ValidationService {

    private final PostcodeValidator mapitPostcodeValidator;

    @Autowired
    public ValidationService(PostcodeValidator mapitPostcodeValidator) {
        this.mapitPostcodeValidator = mapitPostcodeValidator;
    }

    /**
     * Accepts an list of strings and checks for each if mapit data exists.
     * @param postcodes An list of strings which are postcodes
     * @return An list of strings which indicate which postcodes have failed to return geographical information
     */
    public List<String> validatePostcodes(List<String> postcodes) {
        return postcodes.stream()
            .filter(postcode -> !mapitPostcodeValidator.postcodeDataExists(postcode.replaceAll("\\s+","")))
            .collect(toList());
    }
}
