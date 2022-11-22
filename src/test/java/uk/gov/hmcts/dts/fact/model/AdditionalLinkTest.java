package uk.gov.hmcts.dts.fact.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

class AdditionalLinkTest {
    private static final uk.gov.hmcts.dts.fact.entity.AdditionalLink ENTITY = new uk.gov.hmcts.dts.fact.entity.AdditionalLink();

    @BeforeAll
    static void setUp() {
        ENTITY.setUrl("https://test.com");
        ENTITY.setDescription("Description");
        ENTITY.setDescriptionCy("Description cy");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        AdditionalLink additionalLink = new AdditionalLink(ENTITY);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(additionalLink.getUrl()).isEqualTo(ENTITY.getUrl());
        softly.assertThat(additionalLink.getDescription()).isEqualTo(welsh ? ENTITY.getDescriptionCy() : ENTITY.getDescription());
        softly.assertAll();

        LocaleContextHolder.resetLocaleContext();
    }
}
