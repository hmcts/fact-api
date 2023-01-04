package uk.gov.hmcts.dts.fact.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

public final class Utils {
    // The TD postcode area include Scottish Border, Northumberland and Carlisle counties. It is not currently included in the postcode regex to keep it consistently
    // with the implementation on CTF. This regex is currently only used by the deprecated API endpoint (/search/results.json) for the consuming services.
    private static final Pattern SCOTTISH_POSTCODE_AREA_REGEX = Pattern.compile("^(ZE|KW|IV|HS|PH|AB|DD|PA|FK|G[0-9]|KY|KA|DG|EH|ML)", Pattern.CASE_INSENSITIVE);
    private static final String NORTHERN_IRISH_POSTCODE_AREA = "BT";

    private Utils() {

    }

    public static String convertNameToSlug(final String courtName) {
        return courtName.toLowerCase(Locale.getDefault())
            .replaceAll("[^A-Za-z\\d -]", "").trim().replace(" ", "-");
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

    public static boolean isScottishPostcode(final String postcode) {
        return SCOTTISH_POSTCODE_AREA_REGEX.matcher(postcode).find();
    }

    public static boolean isNorthernIrishPostcode(final String postcode) {
        return postcode.toUpperCase(Locale.getDefault()).startsWith(NORTHERN_IRISH_POSTCODE_AREA);
    }

    public static String upperCaseAndStripAllSpaces(final String input) {
        return input.replaceAll("\\s+","").toUpperCase(Locale.getDefault());
    }

    public static List<String> constructAddressLines(final String address) {
        return StringUtils.isBlank(address)
            ? emptyList()
            : address.lines()
                .filter(not(String::isEmpty))
                .collect(toList());
    }

    public static String formatServiceAreasForIntroPara(List<String> serviceAreas, String language) {
        StringBuilder output = new StringBuilder(650);
        ListIterator<String> serviceArea = serviceAreas.listIterator();
        while (serviceArea.hasNext()) {
            output.append(serviceArea.next().toLowerCase(Locale.ROOT));
            int index = serviceArea.nextIndex();
            if (index + 1 == serviceAreas.size()) {
                output.append("en".equals(language) ? " and " : " a ");
                output.append(serviceArea.next().toLowerCase(Locale.ROOT));
                break;
            } else if (index + 1 < serviceAreas.size()) {
                output.append(", ");
            }
        }
        return output.toString();
    }
}
