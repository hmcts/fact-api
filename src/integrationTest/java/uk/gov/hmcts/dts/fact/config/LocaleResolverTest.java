package uk.gov.hmcts.dts.fact.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;

import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@WebMvcTest(LocaleResolver.class)
class LocaleResolverTest {

    @Autowired
    ApplicationContext applicationContext;

    @Mock
    HttpServletRequest request;

    LocaleResolver localeResolver;

    @BeforeEach
    void init() {
        localeResolver = (LocaleResolver) (applicationContext.getBean("localeResolver"));
    }

    @Test
    void shouldReturnLocaleWithLanguageCy() {
        when(request.getHeader("Accept-Language")).thenReturn("cy");
        Locale result = localeResolver.resolveLocale(request);
        assertEquals("cy", result.getLanguage());
    }

    @Test
    void shouldReturnLocaleWithLanguageEn() {
        when(request.getHeader("Accept-Language")).thenReturn("en");
        Locale result = localeResolver.resolveLocale(request);

        assertEquals("en", result.getLanguage());
    }

    @Test
    void shouldReturnNullForNonSupportedLanguage() {
        when(request.getHeader("Accept-Language")).thenReturn("za");
        Locale result = localeResolver.resolveLocale(request);

        assertEquals(null, result);
    }
}
