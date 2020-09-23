package uk.gov.hmcts.dts.fact.exception;

public class SlugNotFoundException extends RuntimeException {


    public SlugNotFoundException(String slug) {
        super("Slug not found: " + slug);
    }

}
