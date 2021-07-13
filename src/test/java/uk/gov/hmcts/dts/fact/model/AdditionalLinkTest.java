package uk.gov.hmcts.dts.fact.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.SidebarLocation;

import java.util.Locale;

public class AdditionalLinkTest {
    private static uk.gov.hmcts.dts.fact.entity.AdditionalLink entity = new uk.gov.hmcts.dts.fact.entity.AdditionalLink();

    @BeforeAll
    static void setUp() {
        entity.setUrl("https://test.com");
        entity.setDescription("Description");
        entity.setDescriptionCy("Description cy");
        entity.setLocation(new SidebarLocation(1, "Find out more about"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        AdditionalLink additionalLink = new AdditionalLink(entity);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(additionalLink.getUrl()).isEqualTo(entity.getUrl());
        softly.assertThat(additionalLink.getDescription()).isEqualTo(welsh ? entity.getDescriptionCy() : entity.getDescription());
        softly.assertThat(additionalLink.getLocation()).isEqualTo(entity.getLocation().getName());
        softly.assertAll();

        LocaleContextHolder.resetLocaleContext();
    }
}
