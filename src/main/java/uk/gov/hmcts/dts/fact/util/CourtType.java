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

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum CourtType {
    MAGISTRATES_COURT("Magistrates' Court", c -> c.getMagistrateCode(), (c, v) -> c.setMagistrateCode(v)),
    CROWN_COURT("Crown Court", c -> c.getNumber(), (c, v) -> c.setNumber(v)),
    COUNTY_COURT("County Court", c -> c.getCciCode(), (c, v) -> c.setCciCode(v)),
    FAMILY_COURT("Family Court"),
    TRIBUNAL("Tribunal");

    private static final Map<String, CourtType> LOOKUP = new ConcurrentHashMap<>();

    private final String name;
    private Function<Court, Integer> courtCodeFunction;
    private BiConsumer<Court, Integer> courtCodeConsumer;

    static {
        Arrays.stream(values())
            .forEach(t -> LOOKUP.put(t.name.toLowerCase(Locale.getDefault()), t));
    }

    public Integer getCourtCodeFromEntity(final Court court) {
        return courtCodeFunction.apply(court);
    }

    public void setCourtCodeInEntity(final Court court, final Integer code) {
        courtCodeConsumer.accept(court, code);
    }

    public static CourtType findByName(final String name) {
        final String key = name.toLowerCase(Locale.getDefault());
        if (!LOOKUP.containsKey(key)) {
            throw new IllegalArgumentException("Unknown court type: " + name);
        }
        return LOOKUP.get(key);
    }
}
