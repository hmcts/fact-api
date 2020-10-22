package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.dts.fact.entity.AddressType;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CourtAddressTest {

    uk.gov.hmcts.dts.fact.entity.CourtAddress entity;

    @BeforeEach
    void init() {
        entity = new uk.gov.hmcts.dts.fact.entity.CourtAddress();
        entity.setAddress("line 1\rline 2\nline 3\r\nline 4");
        entity.setAddressCy("line 1 cy\rline 2 cy\nline 3 cy\r\nline 4 cy");
        final AddressType addressType = new AddressType();
        addressType.setName("Address Type");
        entity.setAddressType(addressType);
        entity.setPostcode("A post code");
        entity.setTownName("A town name");
        entity.setTownNameCy("A town name in Welsh");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        CourtAddress courtAddress = new CourtAddress(entity, welsh);
        assertEquals(welsh ? entity.getAddressCy().lines().collect(toList()) : entity.getAddress().lines().collect(
            toList()), courtAddress.getAddressLines());
        assertEquals(entity.getAddressType().getName(), courtAddress.getAddressType());
        assertEquals(entity.getPostcode(), courtAddress.getPostcode());
        assertEquals(welsh ? entity.getTownNameCy() : entity.getTownName(), courtAddress.getTownName());
    }

    @Test
    void shouldHandleNullAddress() {
        entity.setAddressCy(null);
        CourtAddress courtAddress = new CourtAddress(entity, true);
        assertNull(courtAddress.getAddressLines());
    }

}
