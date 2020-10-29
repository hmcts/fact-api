package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceAreaTest {

    static uk.gov.hmcts.dts.fact.entity.ServiceArea entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.ServiceArea();
        entity.setName("Name");
        entity.setNameCy("Name in Welsh");
        entity.setDescription("Description");
        entity.setDescriptionCy("Description in Welsh");

    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        ServiceArea service = new ServiceArea(entity);
        assertEquals(welsh ? entity.getNameCy() : entity.getName(), service.getName());
        assertEquals(welsh ? entity.getDescriptionCy() : entity.getDescription(), service.getDescription());

        LocaleContextHolder.resetLocaleContext();
    }

}
