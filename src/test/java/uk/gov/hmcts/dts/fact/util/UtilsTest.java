package uk.gov.hmcts.dts.fact.util;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.dts.fact.util.Utils.*;

class UtilsTest {

    protected static final String ENGLISH = "english";
    protected static final String WELSH = "welsh";

    @Test
    void testHtmlFilter() {
        String text = "<p>Text that needs html stripping</p>";
        assertEquals("Text that needs html stripping", stripHtmlFromString(text));
    }

    @Test
    void testUrlDecoder() {
        String text = "https%3A//www.gov.uk/test-url";
        assertEquals("https://www.gov.uk/test-url", decodeUrlFromString(text));
    }

    @Test
    void shouldReturnEmptyIfUrlNull() {
        assertEquals("", decodeUrlFromString(null));
    }

    @Test
    void shouldReturnEnglish() {
        assertEquals(ENGLISH, chooseString(WELSH, ENGLISH));
    }

    @Test
    void shouldReturnEnglishIfWelshNull() {
        LocaleContextHolder.setLocale(new Locale("cy"));
        assertEquals(ENGLISH, chooseString(null, ENGLISH));
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void shouldReturnEnglishIfWelshBlank() {
        LocaleContextHolder.setLocale(new Locale("cy"));
        assertEquals(ENGLISH, chooseString(" ", ENGLISH));
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void shouldReturnWelsh() {
        LocaleContextHolder.setLocale(new Locale("cy"));
        assertEquals(WELSH, chooseString(WELSH, ENGLISH));
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void testUpperCaseAndStripAllSpaces() {
        assertThat(upperCaseAndStripAllSpaces(" b 1 7Pt ")).isEqualTo("B17PT");
    }

    @Test
    void testConstructAddressLines() {
        final String address = "\r\n1 High Street\r\n\r\nLondon\r\n";
        final List<String> addressLines = constructAddressLines(address);
        assertThat(addressLines).hasSize(2);
        assertThat(addressLines.get(0)).isEqualTo("1 High Street");
        assertThat(addressLines.get(1)).isEqualTo("London");
    }

    @Test
    void testConstructAddressLinesForNullAddress() {
        assertThat(constructAddressLines(null))
            .isNotNull()
            .isEmpty();
    }
}
