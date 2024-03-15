package uk.gov.hmcts.dts.fact.exception;

public class DuplicatedListItemException extends RuntimeException {

    private static final long serialVersionUID = -4783211581572957241L;

    /**
     * Constructs a new duplicated list item exception with the specified detail message.
     *
     * @param message the detail message
     */
    public DuplicatedListItemException(final String message) {
        super(message);
    }
}
