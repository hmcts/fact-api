package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AreaOfLawTest {

    static uk.gov.hmcts.dts.fact.entity.AreaOfLaw entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.AreaOfLaw();
        entity.setName("Name of area of law");
        entity.setExternalLink("external link");
        entity.setExternalLinkDescription("description of external link");
        entity.setExternalLinkDescriptionCy("description of external link in Welsh");
        entity.setDisplayName("display name in english");
        entity.setDisplayNameCy("display name in welsh");
        entity.setAltName("display name in english");
        entity.setAltNameCy("display name in welsh");
        entity.setDisplayExternalLink("display external link");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        AreaOfLaw areaOfLaw = new AreaOfLaw(entity);

        assertEquals(entity.getName(), areaOfLaw.getName());
        assertEquals(entity.getExternalLink(), areaOfLaw.getExternalLink());
        assertEquals(
            welsh ? entity.getExternalLinkDescriptionCy() : entity.getExternalLinkDescription(),
            areaOfLaw.getExternalLinkDescription()
        );
        assertEquals(
            welsh ? entity.getDisplayNameCy() : entity.getDisplayName(),
            areaOfLaw.getDisplayName()
        );
        assertEquals(entity.getDisplayExternalLink(), areaOfLaw.getDisplayExternalLink());

        LocaleContextHolder.resetLocaleContext();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testDisplayNameForInPersonCourt(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        final AreaOfLaw areaOfLaw = new AreaOfLaw(entity, true);
        assertThat(areaOfLaw.getDisplayName()).isEqualTo(welsh ? entity.getAltNameCy() : entity.getAltName());

        LocaleContextHolder.resetLocaleContext();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testDisplayForNotInPersonCourt(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        final AreaOfLaw areaOfLaw = new AreaOfLaw(entity, true);
        assertThat(areaOfLaw.getDisplayName()).isEqualTo(welsh ? entity.getDisplayNameCy() : entity.getDisplayName());

        LocaleContextHolder.resetLocaleContext();
    }

}
