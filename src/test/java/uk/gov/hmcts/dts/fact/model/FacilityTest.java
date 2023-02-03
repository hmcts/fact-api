package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FacilityTest {
    private static uk.gov.hmcts.dts.fact.entity.Facility entity;

    @BeforeAll
    static void setUp() {
        uk.gov.hmcts.dts.fact.entity.FacilityType facilityType = new uk.gov.hmcts.dts.fact.entity.FacilityType();
        facilityType.setName("A name");
        facilityType.setNameCy("A name but in Welsh");

        entity = new uk.gov.hmcts.dts.fact.entity.Facility();
        entity.setDescription("A description");
        entity.setDescriptionCy("A description in Welsh");
        entity.setFacilityType(facilityType);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        Facility facility = new Facility(entity);
        assertEquals(welsh ? entity.getFacilityType().getNameCy() : entity.getFacilityType().getName(), facility.getName());
        assertEquals(welsh ? entity.getDescriptionCy() : entity.getDescription(), facility.getDescription());

        LocaleContextHolder.resetLocaleContext();
    }

}
