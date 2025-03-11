package uk.gov.hmcts.dts.fact.services.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.exception.InvalidEpimIdException;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Service
public class ValidationService {

    private final PostcodeValidator mapitPostcodeValidator;
    private final LocalAuthorityValidator mapitlocalAuthorityValidator;
    private static final Pattern EPIM_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{0,30}$");

    /**
     * Constructor for the ValidationService.
     * @param mapitPostcodeValidator The postcode validator
     * @param mapitLocalAuthorityValidator The local authority validator
     */
    @Autowired
    public ValidationService(PostcodeValidator mapitPostcodeValidator, LocalAuthorityValidator mapitLocalAuthorityValidator) {
        this.mapitPostcodeValidator = mapitPostcodeValidator;
        this.mapitlocalAuthorityValidator = mapitLocalAuthorityValidator;
    }

    /**
     * Accepts an list of strings and checks for each if mapit data exists.
     * @param postcodes A list of strings which are postcodes
     * @return A list of strings which indicate which postcodes have failed to return geographical information
     */
    public List<String> validatePostcodes(List<String> postcodes) {
        return postcodes.stream()
            .filter(postcode -> !mapitPostcodeValidator.postcodeDataExists(postcode.replaceAll("\\s+","")))
            .collect(toList());
    }

    /**
     * Accepts an list of full postcodes and checks for each if mapit data exists.
     * @param postcodes A list of strings which are postcodes
     * @return A list of strings which indicate which postcodes have failed to return geographical information
     */
    public List<String> validateFullPostcodes(List<String> postcodes) {
        return postcodes.stream()
            .filter(postcode -> !mapitPostcodeValidator.fullPostcodeValid(postcode.replaceAll("\\s+","")))
            .collect(toList());
    }

    /**
     * Validates that the given local authority name exists as a local authority.
     * @param localAuthorityName The local authority name
     * @return A boolean indicating if the local authority name is valid or not
     */
    public boolean validateLocalAuthority(final String localAuthorityName) {
        return mapitlocalAuthorityValidator.localAuthorityNameIsValid(localAuthorityName);
    }

    /**
     * Validates that the given epimId is alphanumeric and contains dashes only (to 30 chars).
     * @param courtAddresses A list of court addresses
     * @return The epimId if it fails the regex, otherwise InvalidEpimIdException
     */
    public void validateEpimIds(List<CourtAddress> courtAddresses) {
        String epimId = courtAddresses.get(0).getEpimId(); // only primary address has epim id

        if (epimId != null && !EPIM_ID_PATTERN.matcher(epimId).matches()) {
            throw new InvalidEpimIdException(epimId);
        }
    }
}
