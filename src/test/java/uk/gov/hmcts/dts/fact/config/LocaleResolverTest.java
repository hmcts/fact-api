package uk.gov.hmcts.dts.fact.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import uk.gov.hmcts.dts.fact.util.Utils;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocaleResolverTest {

    @Test
    void testResolveLocaleForEnglish() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.example.com");
        request.addHeader("Accept-Language", "en");

        LocaleResolver lr = new LocaleResolver();
        assertEquals("en", lr.resolveLocale(request).toString());
    }

    @Test
    void testResolveLocaleForWelsh() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.example.com");
        request.addHeader("Accept-Language", "cy");

        LocaleResolver lr = new LocaleResolver();
        assertEquals("cy", lr.resolveLocale(request).toString());
    }

    @Test
    void testResolveLocaleForEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.example.com");

        LocaleResolver lr = new LocaleResolver();
        assertEquals(Locale.getDefault(), lr.resolveLocale(request));
    }
}
