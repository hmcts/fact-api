package uk.gov.hmcts.dts.fact.exception;

public class LockExistsException extends RuntimeException {

    private static final long serialVersionUID = 6835667944016309088L;

    public LockExistsException(final String message) {
        super(message);
    }

    public LockExistsException(Exception exception) {
        super(exception);
    }
}
