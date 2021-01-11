package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.Email;
import uk.gov.hmcts.dts.fact.entity.Facility;
import uk.gov.hmcts.dts.fact.entity.FacilityType;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;

import java.util.List;
import java.util.Locale;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourtTest {
    static uk.gov.hmcts.dts.fact.entity.Court courtEntity;

    @BeforeAll
    static void setUp() {
        courtEntity = new uk.gov.hmcts.dts.fact.entity.Court();

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
        addressType.setName("Visit or contact us");
        courtAddress.setAddressType(addressType);
        courtAddress.setPostcode("A post code");
        courtAddress.setTownName("A town name");
        courtEntity.setAddresses(singletonList(courtAddress));

        final Email emailEntity = new Email();
        emailEntity.setAddress("email address");
        emailEntity.setDescription("email address description");
        emailEntity.setExplanation("explanation for email address");
        CourtEmail courtEmailEntity = new CourtEmail();
        courtEmailEntity.setEmail(emailEntity);
        courtEntity.setCourtEmails(singletonList(courtEmailEntity));

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
        final CourtOpeningTime courtOpeningTimeEntity = new CourtOpeningTime();
        courtOpeningTimeEntity.setOpeningTime(openingTimeEntity);
        courtEntity.setCourtOpeningTimes(singletonList(courtOpeningTimeEntity));

        courtEntity.setFacilities(createFacilities());

        courtEntity.setInfo("<p>Info on court</p>");
        courtEntity.setInfoCy("<p>Info on court in Welsh</p>");
        courtEntity.setName("Name");
        courtEntity.setNameCy("Name in Welsh");
        courtEntity.setDirections("Directions");
        courtEntity.setDirectionsCy("Directions in Welsh");
        courtEntity.setAlert("Alert");
        courtEntity.setAlertCy("Alert in Welsh");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreationOfCourt(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        Court court = new Court(courtEntity);
        assertEquals(welsh ? "Name in Welsh" : "Name", court.getName());
        assertEquals(welsh ? "<p>Info on court in Welsh</p>" : "<p>Info on court</p>", court.getInfo());
        assertEquals(welsh ? "Directions in Welsh" : "Directions", court.getDirections());
        assertEquals(welsh ? "Alert in Welsh" : "Alert", court.getAlert());
        assertEquals(courtEntity.getInPerson().getIsInPerson(), court.getInPerson());
        assertEquals(courtEntity.getInPerson().getAccessScheme(), court.getAccessScheme());
        assertEquals("Visit or contact us", court.getAddresses().get(0).getAddressType());
        assertEquals("http://url", court.getAreasOfLaw().get(0).getExternalLink());
        assertEquals(courtEntity.getContacts().get(0).getNumber(), court.getDxNumbers().get(0));

        final uk.gov.hmcts.dts.fact.model.Facility facility = court.getFacilities().get(0);
        assertEquals(
            welsh ? "Description of facility in Welsh" : "Description of facility",
            facility.getDescription()
        );
        assertEquals(2, facility.getOrder());
        assertEquals(5, court.getFacilities().get(1).getOrder());
        assertEquals(10, court.getFacilities().get(2).getOrder());
        assertEquals(MAX_VALUE, court.getFacilities().get(3).getOrder());

        assertEquals(courtEntity.getServiceAreas().get(0).getName(), court.getServiceAreas().get(0));

        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void testNotInPersonData() {
        courtEntity.setInPerson(null);
        final CourtAddress courtAddress = new CourtAddress();
        courtAddress.setAddress("line 1\rline 2\nline3\r\nline4");
        final AddressType addressType = new AddressType();
        addressType.setName("Write to us");
        courtAddress.setAddressType(addressType);
        courtAddress.setPostcode("A post code");
        courtAddress.setTownName("A town name");
        courtEntity.setAddresses(singletonList(courtAddress));
        Court court = new Court(courtEntity);
        assertTrue(court.getInPerson());
        assertNull(court.getAccessScheme());
        assertEquals("Write to us", court.getAddresses().get(0).getAddressType());
    }

    @Test
    void testInPersonData() {
        final CourtAddress courtAddress = new CourtAddress();
        courtAddress.setAddress("line 1\rline 2\nline3\r\nline4");
        final AddressType addressType = new AddressType();
        addressType.setName("Visit us");
        courtAddress.setAddressType(addressType);
        courtAddress.setPostcode("A post code");
        courtAddress.setTownName("A town name");
        courtEntity.setAddresses(singletonList(courtAddress));
        Court court = new Court(courtEntity);
        assertEquals("Visit us", court.getAddresses().get(0).getAddressType());
    }

    private static List<Facility> createFacilities() {
        return asList(
            createFacilityWithOrderOf(10),
            createFacilityWithOrderOf(2),
            createFacilityWithOrderOf(5),
            createFacilityWith(null)
        );
    }

    private static Facility createFacilityWithOrderOf(final int order) {
        final FacilityType facilityType = new FacilityType();
        facilityType.setOrder(order);
        return createFacilityWith(facilityType);
    }

    private static Facility createFacilityWith(final FacilityType facilityType) {
        final Facility facility = new Facility();
        facility.setDescription("<p>Description of facility</p>");
        facility.setDescriptionCy("<p>Description of facility in Welsh</p>");
        facility.setName("Facility");
        facility.setFacilityType(facilityType);
        return facility;
    }
}
