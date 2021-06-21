package uk.gov.hmcts.dts.fact.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InvalidPostcodeException extends RuntimeException {

    private static final long serialVersionUID = 1580364997241986088L;
    private List<String> invalidPostcodes = new ArrayList<>();

    public InvalidPostcodeException(final String postcode) {
        super("Invalid postcode: " + postcode);
    }

    public InvalidPostcodeException(final List<String> postcodes) {
        super("Invalid postcodes: " + postcodes);
        invalidPostcodes.addAll(postcodes);
    }
}
