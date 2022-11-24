package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationUpdateTest {
    private static uk.gov.hmcts.dts.fact.entity.ApplicationUpdate entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.ApplicationUpdate();
        entity.setType("test@test.com");
        entity.setTypeCy("An email address");
        entity.setExternalLink("An email address but in Welsh");
        entity.setExternalLinkDescription("You email it.");
        entity.setExternalLinkDescriptionCy("You email it in Welsh.");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        ApplicationUpdate applicationUpdate = new ApplicationUpdate(entity);
        assertThat(applicationUpdate.getType()).isEqualTo(welsh ? entity.getTypeCy()
                                                              : entity.getType());
        assertThat(applicationUpdate.getEmail()).isNull();
        assertThat(applicationUpdate.getExternalLink()).isEqualTo(entity.getExternalLink());
        assertThat(applicationUpdate.getExternalLinkDescription()).isEqualTo(welsh ? entity.getExternalLinkDescriptionCy()
                                                                                 : entity.getExternalLinkDescription());

        LocaleContextHolder.resetLocaleContext();
    }
}
