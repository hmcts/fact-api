package uk.gov.hmcts.dts.fact.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enum for address type.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum AddressType {
    VISIT_US("Visit us", true),
    WRITE_TO_US("Write to us", false),
    VISIT_OR_CONTACT_US("Visit or contact us", true);

    private static final Map<String, AddressType> LOOKUP = new ConcurrentHashMap<>();

    private final String name;
    private final boolean isACourtAddress;

    static {
        Arrays.stream(values())
            .forEach(t -> LOOKUP.put(t.name.toLowerCase(Locale.getDefault()), t));
    }

    /**
     * Find address type by name.
     *
     * @param name the name
     * @return the address type
     */
    public static AddressType findByName(final String name) {
        final String key = name.toLowerCase(Locale.getDefault());
        if (!LOOKUP.containsKey(key)) {
            throw new IllegalArgumentException("Unknown address type: " + name);
        }
        return LOOKUP.get(key);
    }

    /**
     * Is court address boolean.
     *
     * @param name the name
     * @return the boolean
     */
    public static boolean isCourtAddress(final String name) {
        return findByName(name).isACourtAddress;
    }
}
