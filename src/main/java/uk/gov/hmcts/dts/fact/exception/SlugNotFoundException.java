package uk.gov.hmcts.dts.fact.exception;

public class SlugNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6579941826346533850L;

    public SlugNotFoundException(String slug) {
        super("Slug not found: " + slug);
    }

}
