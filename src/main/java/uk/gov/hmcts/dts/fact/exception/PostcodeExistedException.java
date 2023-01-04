package uk.gov.hmcts.dts.fact.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostcodeExistedException extends RuntimeException {
    private static final long serialVersionUID = -687728108804146149L;
    private final List<String> invalidPostcodes = new ArrayList<>();

    public PostcodeExistedException(final List<String> postcodes) {
        super("Postcodes already exist: " + postcodes);
        invalidPostcodes.addAll(postcodes);
    }
}
