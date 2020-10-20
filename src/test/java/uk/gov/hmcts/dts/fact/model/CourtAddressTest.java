package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.AddressType;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtAddressTest {

    @Test
    void testCreation() {
        uk.gov.hmcts.dts.fact.entity.CourtAddress entity = new uk.gov.hmcts.dts.fact.entity.CourtAddress();
        entity.setAddress("line 1\rline 2\nline3\r\nline4");
        final AddressType addressType = new AddressType();
        addressType.setName("Address Type");
        entity.setAddressType(addressType);
        entity.setPostcode("A post code");
        entity.setTownName("A town name");

        CourtAddress courtAddress = new CourtAddress(entity);

        assertEquals(entity.getAddress().lines().collect(toList()), courtAddress.getAddressLines());
        assertEquals(entity.getAddressType().getName(), courtAddress.getAddressType());
        assertEquals(entity.getPostcode(), courtAddress.getPostcode());
        assertEquals(entity.getTownName(), courtAddress.getTownName());
    }

}
