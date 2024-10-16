package uk.gov.hmcts.dts.fact.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InvalidEpimIdException extends RuntimeException {

    private static final long serialVersionUID = 1580364997241986088L;
    private List<String> invalidEpimIds = new ArrayList<>();

    /**
     * Constructs a new invalid epim id exception with the specified detail message.
     *
     * @param epimIds list of epim ids
     */
    public InvalidEpimIdException(final List<String> epimIds) {
        super("Invalid epimIds: " + epimIds);
        invalidEpimIds.addAll(epimIds);
    }
}
