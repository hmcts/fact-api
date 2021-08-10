package uk.gov.hmcts.dts.fact.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum SidebarLocation {
    THIS_LOCATION_HANDLES("This location handles"),
    FIND_OUT_MORE_ABOUT("Find out more about");

    private final String name;
}
