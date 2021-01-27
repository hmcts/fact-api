package uk.gov.hmcts.dts.fact.model;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.Email;
import uk.gov.hmcts.dts.fact.entity.Facility;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        AddressType addressType = new AddressType();
        courtAddress.setAddressType(addressType);
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
        CourtEmail courtEmail = mock(CourtEmail.class);
        Email email = new Email();
        when(courtEmail.getEmail()).thenReturn(email);
        courtEntity.setCourtEmails(asList(courtEmail));
        Contact contact = new Contact();
        courtEntity.setContacts(asList(contact));
        CourtOpeningTime courtOpeningTime = mock(CourtOpeningTime.class);
        OpeningTime openingTime = new OpeningTime();
        when(courtOpeningTime.getOpeningTime()).thenReturn(openingTime);
        courtEntity.setCourtOpeningTimes(asList(courtOpeningTime));
    }

    @Test
    void testCreation() {
        CourtForDownload court = new CourtForDownload(courtEntity);
        assertEquals("Name", court.getName());
        assertEquals("open", court.getOpen());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(currentTime), court.getUpdated());
        assertEquals(1, court.getAddresses().size());
        assertEquals("Area of law one, Area of law two", court.getAreasOfLaw());
        assertEquals("court type one, court type two", court.getCourtTypes());
        assertEquals(111, court.getCrownCourtCode());
        assertEquals(222, court.getCountyCourtCode());
        assertEquals(333, court.getMagistratesCourtCode());
        assertEquals("facility one, facility two", court.getFacilities());
        assertEquals(courtEntity.getSlug(), court.getSlug());
        assertEquals(1, court.getEmails().size());
        assertEquals(1, court.getOpeningTimes().size());
        assertEquals(1, court.getContacts().size());
    }
}
