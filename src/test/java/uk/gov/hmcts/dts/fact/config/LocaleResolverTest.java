package uk.gov.hmcts.dts.fact.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocaleResolverTest {

    public static final String SERVER_NAME = "www.example.com";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    @Test
    void shouldResolveLocaleForEnglish() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName(SERVER_NAME);
        request.addHeader(ACCEPT_LANGUAGE, "en");

        final LocaleResolver lr = new LocaleResolver();
        assertEquals("en", lr.resolveLocale(request).toString());
    }

    @Test
    void shouldResolveLocaleForWelsh() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName(SERVER_NAME);
        request.addHeader(ACCEPT_LANGUAGE, "cy");

        final LocaleResolver lr = new LocaleResolver();
        assertEquals("cy", lr.resolveLocale(request).toString());
    }

    @Test
    void shouldResolveLocaleForNull() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName(SERVER_NAME);

        final LocaleResolver lr = new LocaleResolver();
        assertEquals(Locale.getDefault(), lr.resolveLocale(request));
    }
    
    @Test
    void shouldResolveLocaleForEmpty() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName(SERVER_NAME);
        request.addHeader(ACCEPT_LANGUAGE, "");

        final LocaleResolver lr = new LocaleResolver();
        assertEquals(Locale.getDefault(), lr.resolveLocale(request));
    }
}
