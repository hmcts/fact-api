package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FacilityTest {
    static uk.gov.hmcts.dts.fact.entity.Facility entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.Facility();
        entity.setName("A name");
        entity.setNameCy("A name but in Welsh");
        entity.setDescription("A description");
        entity.setDescriptionCy("A description in Welsh");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        Facility facility = new Facility(entity);
        assertEquals(welsh ? entity.getNameCy() : entity.getName(), facility.getName());
        assertEquals(welsh ? entity.getDescriptionCy() : entity.getDescription(), facility.getDescription());

        LocaleContextHolder.resetLocaleContext();
    }

}
