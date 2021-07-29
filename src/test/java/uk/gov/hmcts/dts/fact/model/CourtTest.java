package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.AdditionalLink;
import uk.gov.hmcts.dts.fact.entity.AddressType;
import uk.gov.hmcts.dts.fact.entity.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.CourtAdditionalLink;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.Email;
import uk.gov.hmcts.dts.fact.entity.Facility;
import uk.gov.hmcts.dts.fact.entity.FacilityType;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.entity.SidebarLocation;

import java.util.List;
import java.util.Locale;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourtTest {
    static uk.gov.hmcts.dts.fact.entity.Court courtEntity;

    private static final String VISIT_US_ADDRESS_TYPE_NAME = "Visit us";
    private static final String WRITE_TO_US_ADDRESS_TYPE_NAME = "Write to us";
    private static final String VISIT_OR_CONTACT_US_ADDRESS_TYPE_NAME = "Visit or contact us";

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
        contactEntity.setDescription("DX");
        contactEntity.setNumber("123");
        contactEntity.setExplanation("Explanation of contact");
        CourtContact courtContactEntity = mock(CourtContact.class);
        when(courtContactEntity.getContact()).thenReturn(contactEntity);
        courtEntity.setCourtContacts(singletonList(courtContactEntity));

        final AreaOfLaw areaOfLawEntity = new AreaOfLaw();
        areaOfLawEntity.setName("Divorce");
        areaOfLawEntity.setExternalLinkDescription("Description of url");
        areaOfLawEntity.setExternalLink("http%3A//url");
        courtEntity.setAreasOfLaw(singletonList(areaOfLawEntity));

        final CourtType courtTypeEntity = new CourtType();
        courtTypeEntity.setName("court type");
        courtEntity.setCourtTypes(singletonList(courtTypeEntity));

        final OpeningTime openingTimeEntity = new OpeningTime();
        openingTimeEntity.setDescription("opening time type");
        openingTimeEntity.setHours("opening times");
        final CourtOpeningTime courtOpeningTimeEntity = new CourtOpeningTime();
        courtOpeningTimeEntity.setOpeningTime(openingTimeEntity);
        courtEntity.setCourtOpeningTimes(singletonList(courtOpeningTimeEntity));

        courtEntity.setCourtApplicationUpdates(singletonList(createCourtApplicationUpdateEntity()));
        courtEntity.setCourtAdditionalLinks(createCourtAdditionalLinks());
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

        final Court court = new Court(courtEntity);
        assertEquals(welsh ? "Name in Welsh" : "Name", court.getName());
        assertEquals(welsh ? "<p>Info on court in Welsh</p>" : "<p>Info on court</p>", court.getInfo());
        assertEquals(welsh ? "Directions in Welsh" : "Directions", court.getDirections());
        assertEquals(welsh ? "Alert in Welsh" : "Alert", court.getAlert());
        assertEquals(courtEntity.getInPerson().getIsInPerson(), court.getInPerson());
        assertEquals(courtEntity.getInPerson().getAccessScheme(), court.getAccessScheme());
        assertEquals("Visit or contact us", court.getAddresses().get(0).getAddressType());
        assertEquals("http://url", court.getAreasOfLaw().get(0).getExternalLink());
        assertEquals(courtEntity.getCourtContacts().get(0).getContact().getNumber(), court.getDxNumbers().get(0));

        assertEquals(welsh ? courtEntity.getCourtApplicationUpdates().get(0).getApplicationUpdate().getTypeCy()
                         : courtEntity.getCourtApplicationUpdates().get(0).getApplicationUpdate().getType(),
                     court.getApplicationUpdates().get(0).getType());
        assertEquals(courtEntity.getCourtApplicationUpdates().get(0).getApplicationUpdate().getEmail(),
                     court.getApplicationUpdates().get(0).getEmail());

        verifyAdditionalLinks(court.getAdditionalLinks(), courtEntity, welsh);

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

    @Test
    void testVisitUsOrVisitContactUsAddressesSortedFirst() {
        final CourtAddress courtAddressEntity1 = new CourtAddress();
        final AddressType addressType1 = new AddressType();
        addressType1.setName(VISIT_OR_CONTACT_US_ADDRESS_TYPE_NAME);
        courtAddressEntity1.setAddressType(addressType1);

        final CourtAddress courtAddressEntity2 = new CourtAddress();
        final AddressType addressType2 = new AddressType();
        addressType2.setName(WRITE_TO_US_ADDRESS_TYPE_NAME);
        courtAddressEntity2.setAddressType(addressType2);

        final CourtAddress courtAddressEntity3 = new CourtAddress();
        final AddressType addressType3 = new AddressType();
        addressType3.setName(WRITE_TO_US_ADDRESS_TYPE_NAME);
        courtAddressEntity3.setAddressType(addressType3);

        final CourtAddress courtAddressEntity4 = new CourtAddress();
        final AddressType addressType4 = new AddressType();
        addressType4.setName(VISIT_US_ADDRESS_TYPE_NAME);
        courtAddressEntity4.setAddressType(addressType4);

        final uk.gov.hmcts.dts.fact.entity.Court courtEntity = new uk.gov.hmcts.dts.fact.entity.Court();
        courtEntity.setAddresses(asList(courtAddressEntity1, courtAddressEntity2, courtAddressEntity3, courtAddressEntity4));
        courtEntity.setCourtTypes(emptyList());
        courtEntity.setServiceAreas(emptyList());

        final Court court = new Court(courtEntity);
        final List<uk.gov.hmcts.dts.fact.model.CourtAddress> addresses = court.getAddresses();
        assertEquals(VISIT_OR_CONTACT_US_ADDRESS_TYPE_NAME, addresses.get(0).getAddressType());
        assertEquals(VISIT_US_ADDRESS_TYPE_NAME, addresses.get(1).getAddressType());
        assertEquals(WRITE_TO_US_ADDRESS_TYPE_NAME, addresses.get(2).getAddressType());
        assertEquals(WRITE_TO_US_ADDRESS_TYPE_NAME, addresses.get(3).getAddressType());
    }

    private void verifyAdditionalLinks(final List<uk.gov.hmcts.dts.fact.model.AdditionalLink> additionalLinks, final uk.gov.hmcts.dts.fact.entity.Court courtEntity, final boolean welsh) {
        assertEquals(2, additionalLinks.size());

        final AdditionalLink entityAdditionalLinks1 = courtEntity.getCourtAdditionalLinks().get(0).getAdditionalLink();
        assertEquals(entityAdditionalLinks1.getUrl(), additionalLinks.get(0).getUrl());
        assertEquals(welsh ? entityAdditionalLinks1.getDescriptionCy() : entityAdditionalLinks1.getDescription(),
                     additionalLinks.get(0).getDescription());
        assertEquals(entityAdditionalLinks1.getLocation().getName(), additionalLinks.get(0).getLocation());

        final AdditionalLink entityAdditionalLinks2 = courtEntity.getCourtAdditionalLinks().get(1).getAdditionalLink();
        assertEquals(entityAdditionalLinks2.getUrl(), additionalLinks.get(1).getUrl());
        assertEquals(welsh ? entityAdditionalLinks2.getDescriptionCy() : entityAdditionalLinks2.getDescription(),
                     additionalLinks.get(1).getDescription());
        assertEquals(entityAdditionalLinks2.getLocation().getName(), additionalLinks.get(1).getLocation());
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

    private static CourtApplicationUpdate createCourtApplicationUpdateEntity() {
        final ApplicationUpdate applicationUpdateEntity = new ApplicationUpdate();
        applicationUpdateEntity.setType("application update type");
        applicationUpdateEntity.setTypeCy("application update type cy");
        applicationUpdateEntity.setEmail("test@test.com");
        final CourtApplicationUpdate courtApplicationUpdateEntity = new CourtApplicationUpdate();
        courtApplicationUpdateEntity.setApplicationUpdate(applicationUpdateEntity);
        courtApplicationUpdateEntity.setSort(1);
        return courtApplicationUpdateEntity;
    }

    private static List<CourtAdditionalLink> createCourtAdditionalLinks() {
        final AdditionalLink additionalLink1 = new AdditionalLink();
        additionalLink1.setUrl("tester.com");
        additionalLink1.setDescription("tester");
        additionalLink1.setDescriptionCy("tester cy");
        additionalLink1.setLocation(new SidebarLocation(1, "location 1"));

        final CourtAdditionalLink courtAdditionalLink1 = new CourtAdditionalLink();
        courtAdditionalLink1.setAdditionalLink(additionalLink1);
        courtAdditionalLink1.setSort(0);

        final AdditionalLink additionalLink2 = new AdditionalLink();
        additionalLink2.setUrl("developer.com");
        additionalLink2.setDescription("developer");
        additionalLink2.setDescriptionCy("developer cy");
        additionalLink2.setLocation(new SidebarLocation(1, "location 2"));

        final CourtAdditionalLink courtAdditionalLink2 = new CourtAdditionalLink();
        courtAdditionalLink2.setAdditionalLink(additionalLink2);
        courtAdditionalLink2.setSort(0);

        return asList(courtAdditionalLink1, courtAdditionalLink2);
    }
}
