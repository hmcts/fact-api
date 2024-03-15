package uk.gov.hmcts.dts.fact.exception;

public class IllegalListItemException extends RuntimeException {

    private static final long serialVersionUID = 2746953667183065051L;

    /**
     * Constructs a new illegal list item exception with the specified detail message.
     *
     * @param message the detail message
     */
    public IllegalListItemException(final String message) {
        super(message);
    }
}
