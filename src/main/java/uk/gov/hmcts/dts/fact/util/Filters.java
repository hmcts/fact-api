package uk.gov.hmcts.dts.fact.util;

import java.util.function.Predicate;

public final class Filters {

    public static Predicate<uk.gov.hmcts.dts.fact.entity.Contact> nameIsDX = c -> "DX".equalsIgnoreCase(c.getName());
    public static Predicate<uk.gov.hmcts.dts.fact.entity.Contact> nameIsNotDX = nameIsDX.negate();

    private Filters() {

    }

    public static String stripHtmlFromString(String text) {
        if (text != null) {
            return text.replaceAll("\\<.*?>|&nbsp;|amp;", "");
        }
        return "";
    }

}
