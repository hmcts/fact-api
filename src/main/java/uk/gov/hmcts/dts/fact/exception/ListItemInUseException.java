package uk.gov.hmcts.dts.fact.exception;

public class ListItemInUseException extends RuntimeException {

    private static final long serialVersionUID = 6835667944016309088L;

    /**
     * Constructs a new list item in use exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ListItemInUseException(final String message) {
        super(message);
    }

    /**
     * Constructs a new list item in use exception with the specified detail message and inner exception.
     *
     * @param exception the inner exception
     */
    public ListItemInUseException(Exception exception) {
        super(exception);
    }
}
