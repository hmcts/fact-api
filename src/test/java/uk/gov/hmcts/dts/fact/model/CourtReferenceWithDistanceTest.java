package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
            final Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        final CourtReferenceWithDistance court = new CourtReferenceWithDistance(courtEntity);
        assertEquals(welsh ? "Name in Welsh" : "Name", court.getName());
        assertEquals("name-slug", court.getSlug());
        assertEquals("2.2", court.getDistance().toString());

        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void shouldCreateWithNullDistance() {
        courtEntity.setDistance(null);
        final CourtReferenceWithDistance court = new CourtReferenceWithDistance(courtEntity);
        assertEquals("Name", court.getName());
        assertEquals("name-slug", court.getSlug());
        assertNull(court.getDistance());
    }
}
