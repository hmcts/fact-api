package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.AddressType;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtAddress2Test {

    @Test
    void testCreation() {
        uk.gov.hmcts.dts.fact.entity.CourtAddress entity = new uk.gov.hmcts.dts.fact.entity.CourtAddress();
        entity.setAddress("line 1\rline 2\nline3\r\nline4");
        final AddressType addressType = new AddressType();
        addressType.setName("Address Type");
        entity.setAddressType(addressType);
        entity.setPostcode("A post code");
        entity.setTownName("A town name");

        CourtAddress2 courtAddress2 = new CourtAddress2(entity);

        assertEquals(entity.getAddress().lines().collect(toList()), courtAddress2.getAddressLines());
        assertEquals(entity.getAddressType().getName(), courtAddress2.getAddressType());
        assertEquals(entity.getPostcode(), courtAddress2.getPostcode());
        assertEquals(entity.getTownName(), courtAddress2.getTownName());
    }

}
