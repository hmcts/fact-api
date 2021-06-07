package uk.gov.hmcts.dts.fact.exception;

public class MapitUsageException extends RuntimeException {
    private static final long serialVersionUID = 6751592734307424106L;
    private static final String USAGE_LIMIT_REACHED = "Usage limit reached.";

    public MapitUsageException() {
        super(USAGE_LIMIT_REACHED);
    }
}
