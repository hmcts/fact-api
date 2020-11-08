package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt;

import java.util.Locale;

import static java.util.Collections.singletonList;
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
        entity.setSlug("slug");
        entity.setServiceAreaType("type");
        entity.setOnlineUrl("Online url");
        entity.setOnlineText("Online text");
        entity.setOnlineTextCy("Welsh nline text");

        final ServiceAreaCourt serviceAreaCourt = new ServiceAreaCourt();
        serviceAreaCourt.setCatchmentType("national");
        final Court court = new Court();
        court.setName("court");
        serviceAreaCourt.setCourt(court);

        entity.setServiceAreaCourts(singletonList(serviceAreaCourt));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        final ServiceArea service = new ServiceArea(entity);
        assertEquals(welsh ? entity.getNameCy() : entity.getName(), service.getName());
        assertEquals(welsh ? entity.getDescriptionCy() : entity.getDescription(), service.getDescription());
        assertEquals(entity.getSlug(), service.getSlug());
        assertEquals(entity.getServiceAreaType(), service.getServiceAreaType());
        assertEquals(
            entity.getServiceAreaCourts().get(0).getCatchmentType(),
            service.getServiceAreaCourts().get(0).getCatchmentType()
        );
        assertEquals(entity.getOnlineUrl(), service.getOnlineUrl());
        assertEquals(welsh ? entity.getOnlineTextCy() : entity.getOnlineText(), service.getOnlineText());

        LocaleContextHolder.resetLocaleContext();
    }

}
