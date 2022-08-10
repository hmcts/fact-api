package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.County;

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
}
