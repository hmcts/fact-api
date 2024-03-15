package uk.gov.hmcts.dts.fact.exception;

public class LockExistsException extends RuntimeException {

    private static final long serialVersionUID = 6835667944016309088L;

    /**
     * Constructs a new lock exists exception with the specified detail message.
     *
     * @param message the detail message
     */
    public LockExistsException(final String message) {
        super(message);
    }

    /**
     * Constructs a new lock exists exception with the specified detail message and inner exception.
     *
     * @param exception the inner exception
     */
    public LockExistsException(Exception exception) {
        super(exception);
    }
}
