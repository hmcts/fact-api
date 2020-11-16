package uk.gov.hmcts.dts.fact.model;

public enum ServiceAreaType {
    FAMILY,
    CIVIL,
    OTHER;

    public boolean isEqualTo(final String type) {
        return this.toString().equalsIgnoreCase(type);
    }
}
