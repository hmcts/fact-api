package uk.gov.hmcts.dts.fact.exception;

import lombok.Getter;

@Getter
public class InvalidEpimIdException extends RuntimeException {

    private static final long serialVersionUID = 1580364997241986088L;

    /**
     * Constructs a new invalid epim id exception with the specified detail message.
     *
     * @param epimIds list of epim ids
     */
    public InvalidEpimIdException(final String epimIds) {
        super("Invalid epimId: " + epimIds);
    }
}
