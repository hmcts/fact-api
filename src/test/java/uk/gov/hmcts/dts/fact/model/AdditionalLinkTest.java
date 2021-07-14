package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class AdditionalLinkTest {
    private static uk.gov.hmcts.dts.fact.entity.AdditionalLink entity = new uk.gov.hmcts.dts.fact.entity.AdditionalLink();

    @BeforeAll
    static void setUp() {
        entity.setUrl("https://test.com");
        entity.setDescription("Description");
        entity.setDescriptionCy("Description cy");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        AdditionalLink additionalLink = new AdditionalLink(entity);
        assertThat(additionalLink.getUrl()).isEqualTo(entity.getUrl());
        assertThat(additionalLink.getDescription()).isEqualTo(welsh ? entity.getDescriptionCy() : entity.getDescription());

        LocaleContextHolder.resetLocaleContext();
    }
}
