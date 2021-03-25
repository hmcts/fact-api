package uk.gov.hmcts.dts.fact.model;

public enum ServiceAreaType {
    FAMILY,
    CIVIL,
    OTHER;

    public static ServiceAreaType serviceAreaTypeFrom(final String type) {
        try {
            return ServiceAreaType.valueOf(type.toUpperCase());
        } catch (final IllegalArgumentException e) {
            return OTHER;
        }
    }
}
