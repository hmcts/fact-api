package uk.gov.hmcts.dts.fact.mapit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.dts.fact.services.MapitService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class MapitValidator {

    private final MapitService mapitService;
    private static final String fullPostCodeRegex = "([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z]"
        + "[A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y]"
        + "[0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";
    private static final String partialPostCodeRegex = "([Gg][Ii][Rr] 0)|([A-Za-z][0-9]{1,3})|" +
        "([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})\\s?[0-9]?";

    @Autowired
    public MapitValidator(MapitService mapitService) {
        this.mapitService = mapitService;
    }

    public boolean postcodeDataExists(final String postcode) {

        // If we have a full postcode
        if (postcode.matches(fullPostCodeRegex)) {
            return fullPostCodeDataExists(postcode);
        }

        // If we do not have a full postcode, check if we have the outcode specified (2-4 characters)
        // and if we do, check if we have a part of the second half (i.e 1 digit, normally followed by 1-2 characters)
        // If so, then strip out the last half and perform a partial search (which is group 3 below from the regex)
        Matcher m = Pattern.compile(partialPostCodeRegex).matcher(postcode);

        if (m.find()) {

            String partialPostcode = m.group(3);

            // If we send across the partial postcode to mapit and no data is found, try with
            // the first three characters instead, as it may still be valid
            return partialPostCodeDataExists(partialPostcode) ||
                partialPostCodeDataExists(partialPostcode.substring(0, 3));
        }

        // If no match, or failed above based on there being an incomplete postcode
        log.info("Postcode did not match full or partial regex, skipping: {}", postcode);
        return false;
    }

    private boolean fullPostCodeDataExists(String postcode) {
        // If we have a full postcode
        log.info("Full postcode search of mapit data for {}", postcode);
        boolean mapitDataExists = mapitService.getMapitData(postcode).isPresent();

        if(mapitDataExists)
            return true;
        else {
            log.info("No mapit data exists for full postcode provided: {}", postcode);
            return false;
        }
    }

    private boolean partialPostCodeDataExists(String postcode) {
        // We are only looking for the group with the outcode
        log.info("Partial postcode search of mapit data for {}", postcode);
        boolean mapitDataExists = mapitService.getMapitDataWithPartial(postcode).isPresent();

        if(mapitDataExists)
            return true;
        else {
            log.info("No mapit data exists for partial postcode specified for: {}", postcode);
            return false;
        }
    }
}
