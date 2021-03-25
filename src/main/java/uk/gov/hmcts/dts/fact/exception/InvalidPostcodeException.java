package uk.gov.hmcts.dts.fact.exception;

public class InvalidPostcodeException extends RuntimeException {

    private static final long serialVersionUID = 1580364997241986088L;

    public InvalidPostcodeException(String postcode) {
        super("Postcode '" + postcode + "' is not valid");
    }

}
