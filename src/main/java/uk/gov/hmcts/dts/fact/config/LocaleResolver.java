package uk.gov.hmcts.dts.fact.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;

@Configuration
public class LocaleResolver extends AcceptHeaderLocaleResolver {

    List<Locale> locales = asList(
        new Locale("en"),
        new Locale("cy")
    );

    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-Language");
        return headerLang == null || headerLang.isEmpty()
            ? Locale.getDefault()
            : Locale.lookup(Locale.LanguageRange.parse(headerLang), locales);
    }
}

