package uk.gov.hmcts.dts.fact.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.Court;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Enum for court type.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum CourtType {
    MAGISTRATES_COURT("Magistrates' Court", c -> c.getMagistrateCode(), (c, v) -> c.setMagistrateCode(v)),
    CROWN_COURT("Crown Court", c -> c.getNumber(), (c, v) -> c.setNumber(v)),
    COUNTY_COURT("County Court", c -> c.getCciCode(), (c, v) -> c.setCciCode(v)),
    FAMILY_COURT("Family Court", c -> c.getCourtCode(), (c, v) -> c.setCourtCode(v)),
    TRIBUNAL("Tribunal", c -> c.getLocationCode(), (c, v) -> c.setLocationCode(v));

    private static final Map<String, CourtType> LOOKUP = new ConcurrentHashMap<>();

    private final String name;
    private Function<Court, Integer> courtCodeFunction;
    private BiConsumer<Court, Integer> courtCodeConsumer;

    static {
        Arrays.stream(values())
            .forEach(t -> LOOKUP.put(t.name.toLowerCase(Locale.getDefault()), t));
    }

    /**
     * Find court type by name.
     *
     * @param name the name
     * @return the court type
     */
    public Integer getCourtCodeFromEntity(final Court court) {
        return courtCodeFunction.apply(court);
    }

    /**
     * Set court code in entity.
     *
     * @param court the court
     * @param code  the code
     */
    public void setCourtCodeInEntity(final Court court, final Integer code) {
        courtCodeConsumer.accept(court, code);
    }

    /**
     * Find court type by name.
     *
     * @param name the name
     * @return the court type
     */
    public static CourtType findByName(final String name) {
        final String key = name.toLowerCase(Locale.getDefault());
        if (!LOOKUP.containsKey(key)) {
            throw new IllegalArgumentException("Unknown court type: " + name);
        }
        return LOOKUP.get(key);
    }
}
