package uk.gov.hmcts.dts.fact.services.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.services.MapitService;
import uk.gov.hmcts.dts.fact.util.PostcodeArea;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PostcodeValidator {

    private final MapitService mapitService;
    private static final String FULL_POSTCODE = "([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z]"
        + "[A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y]"
        + "[0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";
    private static final String PARTIAL_POSTCODE_SEARCH = "[A-Za-z]{2}";
    private static final String PARTIAL_POSTCODE_NUMERIC = "(^[A-Za-z]{1,2}[0-9]{1,3}$)";
    private static final String PARTIAL_POSTCODE_EDGECASE = "(^[A-Za-z][0-9]{1,2}[A-Za-z][0-9]?$)|"
        + "(^[A-Za-z]{2}([0-9]{1}[A-Za-z][0-9]?)$)";

    @Autowired
    public PostcodeValidator(MapitService mapitService) {
        this.mapitService = mapitService;
    }

    public static boolean isFullPostcodeFormat(final String postcode) {
        return postcode.matches(FULL_POSTCODE);
    }

    public boolean postcodeDataExists(final String postcode) {

        if (StringUtils.isBlank(postcode)) {
            return false;
        } else if (isFullPostcodeFormat(postcode) && fullPostCodeDataExists(postcode)) { // If we have a full postcode
            log.info("Full postcode search result was successful for: {}", postcode);
            return true;
        }

        // Partial postcode search criteria:
        //  - One of two letters only. We check against a valid postcode area list
        //  - One or two letters at the start, two or three digits
        // If the starting is one letter:
        //  - Sub part can be one number or two numbers or three numbers
        //  - Or sub part can be one number, and one letter
        //  - Or two numbers with one letter
        //  - Or sub part can be one number, and one letter, and one number
        //  - Or two numbers with one letter, and one number
        // If the starting is two letters:
        //  - sub part can be one number or two numbers or three numbers
        //  - Or sub part can be one number, and one letter
        //  - Or sub part can be one number, and one letter, and one number
        return PostcodeArea.isValidArea(postcode)
            || partialPostcodeValid(PARTIAL_POSTCODE_NUMERIC, postcode, 1)
            // Check first two characters of the postcode, which will determine the result group we need
            || (postcode.substring(0, 2).matches(PARTIAL_POSTCODE_SEARCH)
                ? partialPostcodeValid(PARTIAL_POSTCODE_EDGECASE, postcode, 2) : // if two letter proceed
                partialPostcodeValid(PARTIAL_POSTCODE_EDGECASE, postcode, 1));   // if one letters proceeds
    }

    public boolean fullPostcodeValid(String postcode) {
        if (StringUtils.isNotBlank(postcode) && isFullPostcodeFormat(postcode)) {
            return fullPostCodeDataExists(postcode);
        }
        log.info("'{}' is not a full postcode", postcode);
        return false;
    }

    private boolean fullPostCodeDataExists(String postcode) {
        log.info("Full postcode search of mapit data for: {}", postcode);
        boolean mapitDataExists = mapitService.getMapitData(postcode).isPresent();

        if (mapitDataExists) {
            return true;
        }

        log.info("No mapit data exists for full postcode provided: {}", postcode);
        return false;
    }

    private boolean partialPostcodeValid(String regex, String postcode, int resultGroup) {
        Matcher matcher = Pattern.compile(regex).matcher(postcode);

        if (matcher.find()) {
            String partialPostcode = matcher.group(resultGroup);
            log.debug("Partial postcode found based on regex was {}, result group was {}",
                     partialPostcode, resultGroup);

            if (StringUtils.isEmpty(partialPostcode)) {
                log.warn("No partial postcode was extracted from the match based on the provided group of {}",
                         resultGroup);
                return false;
            }

            // If we send across the partial postcode to mapit and no data is found, try with
            // the letters and remove the last number, as it may still be valid
            return partialPostCodeDataExists(partialPostcode)
                || partialPostCodeDataExists(partialPostcode.substring(0, partialPostcode.length() - 1));
        }

        log.info("Match failed for partial postcode search for: {}, based on regex {}", postcode, regex);
        return false;
    }

    private boolean partialPostCodeDataExists(String postcode) {
        // We are only looking for the group with the outcode
        if (mapitService.getMapitDataWithPartial(postcode).isPresent()) {
            log.info("Partial postcode search of mapit data for {} was found", postcode);
            return true;
        }

        log.info("No mapit data exists for partial postcode specified for: {}", postcode);
        return false;
    }
}
