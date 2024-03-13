package uk.gov.hmcts.dts.fact.exception;

public class MapitUsageException extends RuntimeException {
    private static final long serialVersionUID = 6751592734307424106L;
    private static final String USAGE_LIMIT_REACHED = "Usage limit reached.";

    /**
     * Constructs a new mapit usage exception with the specified detail message.
     */
    public MapitUsageException() {
        super(USAGE_LIMIT_REACHED);
    }
}
