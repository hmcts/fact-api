package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.Email;
import uk.gov.hmcts.dts.fact.entity.Facility;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourtGeneralTest {
    static Court courtEntity;

    @BeforeAll
    static void setUp() {
        courtEntity = new Court();

        final ServiceArea serviceAreaEntity = new ServiceArea();
        serviceAreaEntity.setName("Divorce");
        courtEntity.setServiceAreas(singletonList(serviceAreaEntity));

        final InPerson inPersonEntity = new InPerson();
        inPersonEntity.setIsInPerson(true);
        inPersonEntity.setAccessScheme(false);
        courtEntity.setInPerson(inPersonEntity);

        final CourtAddress courtAddress = new CourtAddress();
        courtAddress.setAddress("line 1\rline 2\nline3\r\nline4");
        final AddressType addressType = new AddressType();
        addressType.setName("Visit us or write to us");
        courtAddress.setAddressType(addressType);
        courtAddress.setPostcode("A post code");
        courtAddress.setTownName("A town name");
        courtEntity.setAddresses(singletonList(courtAddress));

        final Email emailEntity = new Email();
        emailEntity.setAddress("email address");
        emailEntity.setDescription("email address description");
        emailEntity.setExplanation("explanation for email address");
        courtEntity.setEmails(singletonList(emailEntity));

        final Contact contactEntity = new Contact();
        contactEntity.setName("DX");
        contactEntity.setNumber("123");
        contactEntity.setExplanation("Explanation of contact");
        contactEntity.setSortOrder(1);
        courtEntity.setContacts(singletonList(contactEntity));

        final AreaOfLaw areaOfLawEntity = new AreaOfLaw();
        areaOfLawEntity.setName("Divorce");
        areaOfLawEntity.setExternalLinkDescription("Description of url");
        areaOfLawEntity.setExternalLink("http%3A//url");
        courtEntity.setAreasOfLaw(singletonList(areaOfLawEntity));

        final CourtType courtTypeEntity = new CourtType();
        courtTypeEntity.setName("court type");
        courtEntity.setCourtTypes(singletonList(courtTypeEntity));

        final OpeningTime openingTimeEntity = new OpeningTime();
        openingTimeEntity.setType("opening time type");
        openingTimeEntity.setHours("opening times");
        courtEntity.setOpeningTimes(singletonList(openingTimeEntity));

        final Facility facilityEntity = new Facility();
        facilityEntity.setDescription("<p>Description of facility</p>");
        facilityEntity.setDescriptionCy("<p>Description of facility in Welsh</p>");
        facilityEntity.setName("Facility");
        courtEntity.setFacilities(singletonList(facilityEntity));

        courtEntity.setInfo("<p>Info on court</p>");
        courtEntity.setInfoCy("<p>Info on court in Welsh</p>");
        courtEntity.setName("Name");
        courtEntity.setNameCy("Name in Welsh");
        courtEntity.setDirections("Directions");
        courtEntity.setDirectionsCy("Directions in Welsh");
        courtEntity.setAlert("Alert");
        courtEntity.setAlertCy("Alert in Welsh");
        courtEntity.setDisplayed(true);
        courtEntity.setSlug("slug");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreationOfAdminCourt(boolean welsh) {
        CourtGeneral court = new CourtGeneral(courtEntity);

        assertEquals("slug", court.getSlug());
        assertEquals("Name", court.getName());
        assertEquals("Name in Welsh", court.getNameCy());
        assertEquals("<p>Info on court</p>", court.getInfo());
        assertEquals("<p>Info on court in Welsh</p>", court.getInfoCy());
        assertEquals("Alert", court.getAlert());
        assertEquals("Alert in Welsh", court.getAlertCy());
        assertTrue(court.getOpen());
        assertFalse(court.getAccessScheme());
    }
}
