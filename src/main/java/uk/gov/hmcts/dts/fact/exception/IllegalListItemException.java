package uk.gov.hmcts.dts.fact.exception;

public class IllegalListItemException extends RuntimeException {

    private static final long serialVersionUID = 2746953667183065051L;

    public IllegalListItemException(final String message) {
        super(message);
    }
}
