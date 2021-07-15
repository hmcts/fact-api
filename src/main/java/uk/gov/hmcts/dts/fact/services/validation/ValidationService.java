package uk.gov.hmcts.dts.fact.services.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ValidationService {

    private final PostcodeValidator mapitPostcodeValidator;
    private final LocalAuthorityValidator mapitlocalAuthorityValidator;

    @Autowired
    public ValidationService(PostcodeValidator mapitPostcodeValidator, LocalAuthorityValidator mapitLocalAuthorityValidator) {
        this.mapitPostcodeValidator = mapitPostcodeValidator;
        this.mapitlocalAuthorityValidator = mapitLocalAuthorityValidator;
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

    /**
     * Validates that the given local authority name exists as a local authority.
     * @param localAuthorityName The local authority name
     * @return A boolean indicating if the local authority name is valid or not
     */
    public boolean validateLocalAuthority(String localAuthorityName) {
        return mapitlocalAuthorityValidator.localAuthorityNameIsValid(localAuthorityName);
    }
}
