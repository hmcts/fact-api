package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.Facility;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtForDownloadTest {
    static Court courtEntity;
    private static Timestamp currentTime = new Timestamp(System.currentTimeMillis());

    @BeforeAll
    static void setUp() {
        courtEntity = new Court();
        courtEntity.setName("Name");
        courtEntity.setDisplayed(true);
        courtEntity.setUpdatedAt(currentTime);

        CourtAddress courtAddress = new CourtAddress();
        courtAddress.setAddress("Address");
        courtAddress.setTownName("Town");
        courtAddress.setPostcode("Postcode");
        List<CourtAddress> addresses = new ArrayList<>();
        addresses.add(courtAddress);
        courtEntity.setAddresses(addresses);
        AreaOfLaw areaOfLawOne = new AreaOfLaw();
        areaOfLawOne.setName("Area of law one");
        AreaOfLaw areaOfLawTwo = new AreaOfLaw();
        areaOfLawTwo.setName("Area of law two");
        courtEntity.setAreasOfLaw(asList(areaOfLawOne, areaOfLawTwo));
        final CourtType courtTypeEntityOne = new CourtType();
        courtTypeEntityOne.setName("court type one");
        final CourtType courtTypeEntityTwo = new CourtType();
        courtTypeEntityTwo.setName("court type two");
        courtEntity.setCourtTypes(asList(courtTypeEntityOne, courtTypeEntityTwo));
        courtEntity.setNumber(111);
        courtEntity.setCciCode(222);
        courtEntity.setMagistrateCode(333);
        Facility facilityOne = new Facility();
        facilityOne.setName("facility one");
        Facility facilityTwo = new Facility();
        facilityTwo.setName("facility two");
        courtEntity.setFacilities(asList(facilityOne, facilityTwo));
        courtEntity.setSlug("name-slug");
    }

    @Test
    void testCreation() {
        CourtForDownload court = new CourtForDownload(courtEntity);
        assertEquals("Name", court.getName());
        assertEquals("open", court.getOpen());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(currentTime), court.getUpdated());
        assertEquals("Address, Town, Postcode", court.getAddress());
        assertEquals("Area of law one, Area of law two", court.getAreasOfLaw());
        assertEquals("court type one, court type two", court.getCourtTypes());
        assertEquals(111, court.getCrownCourtCode());
        assertEquals(222, court.getCountyCourtCode());
        assertEquals(333, court.getMagistratesCourtCode());
        assertEquals("facility one, facility two", court.getFacilities());
        assertEquals("https://www.find-court-tribunal.service.gov.uk/courts/" + courtEntity.getSlug(), court.getUrl());
    }
}
