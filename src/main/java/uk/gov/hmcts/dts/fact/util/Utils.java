package uk.gov.hmcts.dts.fact.util;

import java.util.function.Predicate;

public final class Utils {

    public static Predicate<uk.gov.hmcts.dts.fact.entity.Contact> nameIsDX = c -> "DX".equalsIgnoreCase(c.getName());
    public static Predicate<uk.gov.hmcts.dts.fact.entity.Contact> nameIsNotDX = nameIsDX.negate();

    private Utils() {

    }

    public static String stripHtmlFromString(String text) {
        if (text != null) {
            return text.replaceAll("\\<.*?>|&nbsp;|amp;", "");
        }
        return "";
    }

    public static String chooseString(boolean welshPreferred, String welsh, String english) {
        return welshPreferred && null != welsh && !welsh.isBlank() ? welsh : english;
    }
}
