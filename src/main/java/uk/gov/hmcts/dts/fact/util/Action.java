package uk.gov.hmcts.dts.fact.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enum for action.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum Action {
    NEAREST("nearest"),
    DOCUMENTS("documents"),
    UPDATE("update"),
    UNDEFINED("undefined");

    public String name;

    private static final Map<String, Action> LOOKUP = new ConcurrentHashMap<>();

    static {
        Arrays.stream(values())
            .forEach(a -> LOOKUP.put(a.name.toLowerCase(Locale.getDefault()), a));
    }

    /**
     * Find action by name.
     *
     * @param name the name
     * @return the action
     */
    public static Action findAction(final String name) {
        final String key = name.toLowerCase(Locale.getDefault());
        if (!LOOKUP.containsKey(key)) {
            throw new IllegalArgumentException("Unknown action: " + name);
        }
        return LOOKUP.get(key);
    }

    /**
     * Is nearest boolean.
     *
     * @param action the action
     * @return the boolean
     */
    public static boolean isNearest(final Action action) {
        return action == findAction("nearest");
    }

}
