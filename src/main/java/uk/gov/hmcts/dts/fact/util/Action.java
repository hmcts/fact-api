package uk.gov.hmcts.dts.fact.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum Action {
    NEAREST("nearest"),
    DOCUMENTS("documents"),
    UPDATE("update"),
    NOT_LISTED("");

    public String name;

    private static final Map<String, Action> LOOKUP = new ConcurrentHashMap<>();

    static {
        Arrays.stream(values())
            .forEach(a -> LOOKUP.put(a.name.toLowerCase(Locale.getDefault()), a));
    }

    public static Action findByName(final String name) {
        final String key = name.toLowerCase(Locale.getDefault());
        if (!LOOKUP.containsKey(key)) {
            throw new IllegalArgumentException("Unknown action: " + name);
        }
        return LOOKUP.get(key);
    }

}
