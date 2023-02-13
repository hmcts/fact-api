package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.Court;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceAreaCourtTest {

    private static uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt();
        entity.setCatchmentType("national");

        final Court court = new Court();
        court.setName("court");
        court.setSlug("slug");
        entity.setCourt(court);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        final ServiceAreaCourt serviceAreaCourt = new ServiceAreaCourt(entity);
        assertEquals(entity.getCatchmentType(), serviceAreaCourt.getCatchmentType());
        assertEquals(entity.getCourt().getName(), serviceAreaCourt.getCourtName());
        assertEquals(entity.getCourt().getSlug(), serviceAreaCourt.getSlug());

        LocaleContextHolder.resetLocaleContext();
    }
}
