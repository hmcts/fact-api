package uk.gov.hmcts.dts.fact.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostcodeNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -4178827964602912131L;
    private List<String> invalidPostcodes = new ArrayList<>();

    public PostcodeNotFoundException(final List<String> postcodes) {
        super("Postcodes do not exist: " + postcodes);
        invalidPostcodes.addAll(postcodes);
    }
}
