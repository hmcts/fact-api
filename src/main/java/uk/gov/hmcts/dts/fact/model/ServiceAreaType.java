package uk.gov.hmcts.dts.fact.model;

public enum ServiceAreaType {
    FAMILY,
    CIVIL,
    OTHER;

    public static ServiceAreaType serviceAreaTypeFrom(final String type) {
        if (FAMILY.isEqualTo(type)) {
            return FAMILY;
        } else if (CIVIL.isEqualTo(type)) {
            return CIVIL;
        }

        return OTHER;
    }

    public boolean isEqualTo(final String type) {
        return this.toString().equalsIgnoreCase(type);
    }
}
