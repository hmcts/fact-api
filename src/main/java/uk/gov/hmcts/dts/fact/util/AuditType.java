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
public enum AuditType {
    UPDATE_COURT_AREAS_OF_LAW("Update court areas of law"),
    UPDATE_LOCAL_AUTHORITY("Update local authority"),
    UPDATE_COURT_ADDITIONAL_LINKS("Update court additional links"),
    UPDATE_ADDRESSES_AND_COORDINATES("Update court addresses and coordinates"),
    UPDATE_COURT_CONTACTS("Update court contacts"),
    UPDATE_COURT_EMAIL_LIST("Update court email list"),
    UPDATE_COURT_FACILITIES("Update court facilities"),
    UPDATE_COURT_GENERAL_INFO("Update court general info"),
    UPDATE_COURT_LOCAL_AUTHORITIES("Update court local authorities"),
    UPDATE_COURT_OPENING_TIMES("Update court opening times"),
    UPDATE_COURT_COURT_TYPES("Update court court types"),
    UPDATE_AREA_OF_LAW("Update area of law"),
    UPDATE_COURT_DETAILS("Update court details"),
    CREATE_AREA_OF_LAW("Create area of law"),
    CREATE_COURT_POSTCODES("Create court postcodes"),
    DELETE_COURT_POSTCODES("Delete court postcodes"),
    MOVE_COURT_POSTCODES("Move court postcodes"),
    CREATE_OPENING_TYPE("Create opening type"),
    UPDATE_OPENING_TYPE("Update opening type"),
    DELETE_OPENING_TYPE("Delete opening type"),
    CREATE_NEW_COURT("Create new court"),
    DELETE_EXISTING_COURT("Delete existing court"),
    UPDATE_COURT_SPOE_AREAS_OF_LAW("Update court spoe areas of law"),
    UPDATE_COURT_APPLICATION_UPDATES_LIST("Update court application updates list"),
    CREATE_COURT_LOCK("Create court lock"),
    UPDATE_COURT_LOCK("Update court lock"),
    DELETE_COURT_LOCK("Delete court lock");

    private final String name;

    private static final Map<String, AuditType> LOOKUP = new ConcurrentHashMap<>();

    static {
        Arrays.stream(values())
            .forEach(t -> LOOKUP.put(t.name.toLowerCase(Locale.getDefault()), t));
    }

    public static String findByName(final String name) {
        final String key = name.toLowerCase(Locale.getDefault());
        if (!LOOKUP.containsKey(key)) {
            throw new IllegalArgumentException("Unknown audit type: " + name);
        }
        return LOOKUP.get(key).getName();
    }
}
