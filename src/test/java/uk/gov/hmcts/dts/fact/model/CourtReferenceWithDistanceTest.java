package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtReferenceWithDistanceTest {
    static CourtWithDistance courtEntity;

    @BeforeAll
    static void setUp() {
        courtEntity = new CourtWithDistance();
        courtEntity.setName("Name");
        courtEntity.setSlug("name-slug");
        courtEntity.setNameCy("Name in Welsh");
        courtEntity.setDistance(2.2);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        CourtReferenceWithDistance court = new CourtReferenceWithDistance(courtEntity);
        assertEquals(welsh ? "Name in Welsh" : "Name", court.getName());
        assertEquals("name-slug", court.getSlug());
        assertEquals("2.20", court.getDistance().toString());


        LocaleContextHolder.resetLocaleContext();
    }


}
