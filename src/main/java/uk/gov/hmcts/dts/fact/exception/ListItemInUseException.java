package uk.gov.hmcts.dts.fact.exception;

public class ListItemInUseException extends RuntimeException {

    private static final long serialVersionUID = 6835667944016309088L;

    public ListItemInUseException(final String message) {
        super(message);
    }

    public ListItemInUseException(Exception exception) {
        super(exception);
    }
}
