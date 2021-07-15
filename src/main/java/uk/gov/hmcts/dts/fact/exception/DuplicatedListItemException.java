package uk.gov.hmcts.dts.fact.exception;

public class DuplicatedListItemException extends RuntimeException {

    private static final long serialVersionUID = -4783211581572957241L;

    public DuplicatedListItemException(final String message) {
        super(message);
    }
}
