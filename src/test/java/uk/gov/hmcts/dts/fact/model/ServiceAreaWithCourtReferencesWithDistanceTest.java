package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceAreaWithCourtReferencesWithDistanceTest {

    static uk.gov.hmcts.dts.fact.entity.ServiceArea entity;
    static List<CourtReferenceWithDistance> courts;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.ServiceArea();
        entity.setSlug("slug");
        entity.setName("name");
        entity.setNameCy("Welsh name");
        entity.setOnlineText("online text");
        entity.setOnlineTextCy("Welsh online text");
        entity.setOnlineUrl("url");

        courts = emptyList();
    }


    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance = new ServiceAreaWithCourtReferencesWithDistance(
            entity, courts);

        assertEquals(entity.getSlug(), serviceAreaWithCourtReferencesWithDistance.getSlug());
        assertEquals(
            welsh ? entity.getNameCy() : entity.getName(),
            serviceAreaWithCourtReferencesWithDistance.getName()
        );
        assertEquals(
            welsh ? entity.getOnlineTextCy() : entity.getOnlineText(),
            serviceAreaWithCourtReferencesWithDistance.getOnlineText()
        );
        assertEquals(entity.getOnlineUrl(), serviceAreaWithCourtReferencesWithDistance.getOnlineUrl());

        assertEquals(courts, serviceAreaWithCourtReferencesWithDistance.getCourts());

        LocaleContextHolder.resetLocaleContext();
    }
}
