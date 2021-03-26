package uk.gov.hmcts.dts.fact.model;

import java.util.Locale;

public enum ServiceAreaType {
    FAMILY,
    CIVIL,
    OTHER;

    public static ServiceAreaType serviceAreaTypeFrom(final String type) {
        try {
            return valueOf(type.toUpperCase(Locale.getDefault()));
        } catch (final IllegalArgumentException e) {
            return OTHER;
        }
    }
}
