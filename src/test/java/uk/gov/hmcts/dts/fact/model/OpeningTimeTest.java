package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpeningTimeTest {

    static uk.gov.hmcts.dts.fact.entity.OpeningTime entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.OpeningTime();
        entity.setId(117);
        entity.setType("A type");
        entity.setTypeCy("A type but in Welsh");
        entity.setHours("A set of hours");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        OpeningTime openingTime = new OpeningTime(entity);
        assertEquals(entity.getHours(), openingTime.getHours());
        assertEquals(welsh ? entity.getTypeCy() : entity.getType(), openingTime.getType());

        LocaleContextHolder.resetLocaleContext();
    }

}
