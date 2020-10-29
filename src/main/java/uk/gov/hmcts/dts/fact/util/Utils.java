package uk.gov.hmcts.dts.fact.util;

import org.springframework.context.i18n.LocaleContextHolder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

public final class Utils {

    public static final Predicate<uk.gov.hmcts.dts.fact.entity.Contact> NAME_IS_DX = c -> "DX".equalsIgnoreCase(c.getName());
    public static final Predicate<uk.gov.hmcts.dts.fact.entity.Contact> NAME_IS_NOT_DX = NAME_IS_DX.negate();

    private Utils() {

    }

    public static String stripHtmlFromString(String text) {
        if (text != null) {
            return text.replaceAll("\\<.*?>|&nbsp;|amp;", "");
        }
        return "";
    }

    public static String decodeUrlFromString(String url) {
        if (url != null) {
            return URLDecoder.decode(url, StandardCharsets.UTF_8);
        }
        return "";
    }

    public static String chooseString(String welsh, String english) {
        boolean welshPreferred = LocaleContextHolder.getLocale().getLanguage().equals("cy");
        return welshPreferred && null != welsh && !welsh.isBlank() ? welsh : english;
    }
}
