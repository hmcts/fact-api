package uk.gov.hmcts.dts.fact.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InvalidPostcodeException extends RuntimeException {

    private static final long serialVersionUID = 1580364997241986088L;
    private List<String> invalidPostcodes = new ArrayList<>();

    public InvalidPostcodeException(final String postcode) {
        super("Postcode '" + postcode + "' is not valid");
    }

    public InvalidPostcodeException(final List<String> postcodes) {
        super("Postcode '" + postcodes + "' is not valid");
        invalidPostcodes.addAll(postcodes);
    }
}
