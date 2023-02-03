package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.Court;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtReferenceTest {
    private static Court courtEntity;
    private static final Timestamp CURRENT_TIME = new Timestamp(System.currentTimeMillis());

    @BeforeAll
    static void setUp() {
        courtEntity = new Court();
        courtEntity.setName("Name");
        courtEntity.setSlug("name-slug");
        courtEntity.setNameCy("Name in Welsh");
        courtEntity.setUpdatedAt(CURRENT_TIME);
        courtEntity.setDisplayed(true);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        CourtReference court = new CourtReference(courtEntity);
        assertEquals(welsh ? "Name in Welsh" : "Name", court.getName());
        assertEquals("name-slug", court.getSlug());
        assertEquals(
            new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(CURRENT_TIME), court.getUpdatedAt());

        LocaleContextHolder.resetLocaleContext();
    }


}
