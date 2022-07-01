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
        //System.out.println(LOOKUP);

        if (!LOOKUP.containsKey(key)) {
            //System.out.println("key NOT in lookup");

            //System.out.println(name + key);
            throw new IllegalArgumentException("Unknown action: " + name);
        }
        //System.out.println("key IS in lookup");

        //System.out.println("name " + name);
        //System.out.println("key " + key);


        //System.out.println("LOOKUP get key " + LOOKUP.get(key));

        return LOOKUP.get(key);
    }

}
