package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.Facility;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class CourtTest {

    @Test
    void testCreationOfCourt() {
        uk.gov.hmcts.dts.fact.entity.Court courtEntity = new uk.gov.hmcts.dts.fact.entity.Court();

        final ServiceArea serviceAreaEntity = new ServiceArea();
        serviceAreaEntity.setService("Divorce");
        courtEntity.setServiceArea(serviceAreaEntity);

        final InPerson inPersonEntity = new InPerson();
        inPersonEntity.setIsInPerson(true);
        courtEntity.setInPerson(inPersonEntity);

        final CourtAddress courtAddress = new CourtAddress();
        courtAddress.setAddress("line 1\rline 2\nline3\r\nline4");
        final AddressType addressType = new AddressType();
        addressType.setName("Visit us or write to us");
        courtAddress.setAddressType(addressType);
        courtAddress.setPostcode("A post code");
        courtAddress.setTownName("A town name");
        courtEntity.setAddresses(Collections.singletonList(courtAddress));

        final CourtEmail courtEmailEntity = new CourtEmail();
        courtEmailEntity.setAddress("email address");
        courtEmailEntity.setDescription("email address description");
        courtEmailEntity.setExplanation("explanation for email address");
        courtEntity.setEmails(Collections.singletonList(courtEmailEntity));

        final Contact contactEntity = new Contact();
        contactEntity.setName("DX");
        contactEntity.setNumber("123");
        contactEntity.setExplanation("Explanation of contact");
        contactEntity.setSortOrder(1);
        courtEntity.setContacts(Collections.singletonList(contactEntity));

        final AreaOfLaw areaOfLawEntity = new AreaOfLaw();
        areaOfLawEntity.setName("Divorce");
        areaOfLawEntity.setExternalLinkDescription("Description of url");
        areaOfLawEntity.setExternalLink("url");
        courtEntity.setAreasOfLaw(Collections.singletonList(areaOfLawEntity));

        final CourtType courtTypeEntity = new CourtType();
        courtTypeEntity.setName("court type");
        courtEntity.setCourtTypes(Collections.singletonList(courtTypeEntity));

        final OpeningTime openingTimeEntity = new OpeningTime();
        openingTimeEntity.setType("opening time type");
        openingTimeEntity.setHours("opening times");
        courtEntity.setOpeningTimes(Collections.singletonList(openingTimeEntity));

        final Facility facilityEntity = new Facility();
        facilityEntity.setDescription("<p>Description of facility</p>");
        facilityEntity.setName("Facility");
        courtEntity.setFacilities(Collections.singletonList(facilityEntity));

        courtEntity.setInfo("<p>Info on court</p>");

        Court court = new Court(courtEntity);

        assertEquals("Info on court", court.getInfo());
        assertEquals(courtEntity.getInPerson().getIsInPerson(), court.getInPerson());
        assertEquals("Visit or contact us", court.getAddresses().get(0).getAddressType());
        assertEquals(courtEntity.getContacts().get(0).getNumber(), court.getDxNumber().get(0));
        assertEquals("Description of facility", court.getFacilities().get(0).getDescription());
        assertEquals(courtEntity.getServiceArea().getService(), court.getServiceArea());
        assertEquals("Info on court", court.getInfo());

    }

}
