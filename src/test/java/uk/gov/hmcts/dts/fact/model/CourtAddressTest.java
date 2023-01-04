package uk.gov.hmcts.dts.fact.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.County;
import uk.gov.hmcts.dts.fact.entity.CourtSecondaryAddressType;
import uk.gov.hmcts.dts.fact.entity.CourtType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtAddressTest {

    private static uk.gov.hmcts.dts.fact.entity.CourtAddress entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.CourtAddress();
        entity.setAddress("line 1\rline 2\nline 3\r\nline 4");
        entity.setAddressCy("line 1 cy\rline 2 cy\nline 3 cy\r\nline 4 cy");
        final AddressType addressType = new AddressType();
        addressType.setName("Address Type");
        addressType.setNameCy("Address Type in Welsh");
        final County county = new County();
        county.setId(1);
        county.setName("County");
        county.setCountry("England");
        entity.setCounty(county);
        entity.setAddressType(addressType);
        entity.setPostcode("A post code");
        entity.setTownName("A town name");
        entity.setTownNameCy("A town name in Welsh");
        entity.setCourtSecondaryAddressType(Arrays.asList(
            new CourtSecondaryAddressType(
                entity,
                new AreaOfLaw(1, "area of law")
            ),
            new CourtSecondaryAddressType(
                entity,
                new CourtType(1, "court type")
            )
        ));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        CourtAddress courtAddress = new CourtAddress(entity);
        assertEquals(welsh ? entity.getAddressCy().lines().collect(toList()) : entity.getAddress().lines().collect(
            toList()), courtAddress.getAddressLines());
        assertEquals(
            welsh ? entity.getAddressType().getNameCy() : entity.getAddressType().getName(),
            courtAddress.getAddressType()
        );
        assertEquals(entity.getPostcode(), courtAddress.getPostcode());
        assertEquals(welsh ? entity.getTownNameCy() : entity.getTownName(), courtAddress.getTownName());
        assertEquals(entity.getCounty().getName(), courtAddress.getCounty());
        assertEquals(
            entity.getCourtSecondaryAddressType().get(0).getAreaOfLaw().getName(),
            "area of law"
        );
        assertEquals(
            entity.getCourtSecondaryAddressType().get(1).getCourtType().getName(),
            "court type"
        );

        LocaleContextHolder.resetLocaleContext();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreationNoCounty(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        entity.setCounty(null);

        CourtAddress courtAddress = new CourtAddress(entity);
        assertEquals(welsh ? entity.getAddressCy().lines().collect(toList()) : entity.getAddress().lines().collect(
            toList()), courtAddress.getAddressLines());
        assertEquals(
            welsh ? entity.getAddressType().getNameCy() : entity.getAddressType().getName(),
            courtAddress.getAddressType()
        );
        assertEquals(entity.getPostcode(), courtAddress.getPostcode());
        assertEquals(welsh ? entity.getTownNameCy() : entity.getTownName(), courtAddress.getTownName());
        assertEquals("", courtAddress.getCounty());
        assertEquals(
            "area of law",
            entity.getCourtSecondaryAddressType().get(0).getAreaOfLaw().getName()
        );
        assertEquals(
            "court type",
            entity.getCourtSecondaryAddressType().get(1).getCourtType().getName()
        );

        LocaleContextHolder.resetLocaleContext();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testNullAddressCreation(boolean welsh) {
        entity.setAddress(null);
        entity.setAddressCy(null);
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        CourtAddress courtAddress = new CourtAddress(entity);
        assertEquals(emptyList(), courtAddress.getAddressLines());

        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void testNoSecondaryAddressTypeNull() {
        uk.gov.hmcts.dts.fact.entity.CourtAddress copyEntity =
            new uk.gov.hmcts.dts.fact.entity.CourtAddress();
        copyEntity.setAddress("line 1\rline 2\nline 3\r\nline 4");
        copyEntity.setAddressCy("line 1 cy\rline 2 cy\nline 3 cy\r\nline 4 cy");
        final AddressType addressType = new AddressType();
        addressType.setName("Address Type");
        addressType.setNameCy("Address Type in Welsh");
        copyEntity.setAddressType(addressType);
        copyEntity.setCourtSecondaryAddressType(null);
        CourtAddress courtAddress = new CourtAddress(copyEntity);
        Assertions.assertThat(courtAddress.getCourtSecondaryAddressType()).isNull();
    }

    @Test
    void testNoSecondaryAddressTypeNullEmpty() {
        uk.gov.hmcts.dts.fact.entity.CourtAddress copyEntity =
            new uk.gov.hmcts.dts.fact.entity.CourtAddress();
        copyEntity.setAddress("line 1\rline 2\nline 3\r\nline 4");
        copyEntity.setAddressCy("line 1 cy\rline 2 cy\nline 3 cy\r\nline 4 cy");
        final AddressType addressType = new AddressType();
        addressType.setName("Address Type");
        addressType.setNameCy("Address Type in Welsh");
        copyEntity.setAddressType(addressType);
        copyEntity.setCourtSecondaryAddressType(new ArrayList<>());
        CourtAddress courtAddress = new CourtAddress(copyEntity);
        Assertions.assertThat(courtAddress.getCourtSecondaryAddressType()).isNull();
    }
}
