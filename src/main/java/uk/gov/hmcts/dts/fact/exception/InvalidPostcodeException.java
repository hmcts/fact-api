package uk.gov.hmcts.dts.fact.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InvalidPostcodeException extends RuntimeException {

    private static final long serialVersionUID = 1580364997241986088L;
    private List<String> invalidPostcodes = new ArrayList<>();

    /**
     * Constructs a new invalid postcode exception with the specified detail message.
     *
     * @param postcode postcode string
     */
    public InvalidPostcodeException(final String postcode) {
        super("Invalid postcode: " + postcode);
    }

    /**
     * Constructs a new invalid postcode exception with the specified detail message.
     *
     * @param postcodes list of postcodes
     */
    public InvalidPostcodeException(final List<String> postcodes) {
        super("Invalid postcodes: " + postcodes);
        invalidPostcodes.addAll(postcodes);
    }
}
