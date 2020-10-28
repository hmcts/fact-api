package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.*;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.Email;
import uk.gov.hmcts.dts.fact.entity.Facility;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtReferenceTest {
    static uk.gov.hmcts.dts.fact.entity.Court courtEntity;
    private static Timestamp currentTime = new Timestamp(System.currentTimeMillis());

    @BeforeAll
    static void setUp() {
        courtEntity = new uk.gov.hmcts.dts.fact.entity.Court();

        final ServiceArea serviceAreaEntity = new ServiceArea();
        serviceAreaEntity.setService("Divorce");
        courtEntity.setServiceArea(serviceAreaEntity);

        final InPerson inPersonEntity = new InPerson();
        inPersonEntity.setIsInPerson(true);
        courtEntity.setInPerson(inPersonEntity);

        final uk.gov.hmcts.dts.fact.entity.CourtAddress courtAddress = new CourtAddress();
        courtAddress.setAddress("line 1\rline 2\nline3\r\nline4");
        final AddressType addressType = new AddressType();
        addressType.setName("Visit us or write to us");
        courtAddress.setAddressType(addressType);
        courtAddress.setPostcode("A post code");
        courtAddress.setTownName("A town name");
        courtEntity.setAddresses(Collections.singletonList(courtAddress));

        final uk.gov.hmcts.dts.fact.entity.Email emailEntity = new Email();
        emailEntity.setAddress("email address");
        emailEntity.setDescription("email address description");
        emailEntity.setExplanation("explanation for email address");
        courtEntity.setEmails(Collections.singletonList(emailEntity));

        final uk.gov.hmcts.dts.fact.entity.Contact contactEntity = new uk.gov.hmcts.dts.fact.entity.Contact();
        contactEntity.setName("DX");
        contactEntity.setNumber("123");
        contactEntity.setExplanation("Explanation of contact");
        contactEntity.setSortOrder(1);
        courtEntity.setContacts(Collections.singletonList(contactEntity));

        final uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLawEntity = new AreaOfLaw();
        areaOfLawEntity.setName("Divorce");
        areaOfLawEntity.setExternalLinkDescription("Description of url");
        areaOfLawEntity.setExternalLink("http%3A//url");
        courtEntity.setAreasOfLaw(Collections.singletonList(areaOfLawEntity));

        final CourtType courtTypeEntity = new CourtType();
        courtTypeEntity.setName("court type");
        courtEntity.setCourtTypes(Collections.singletonList(courtTypeEntity));

        final uk.gov.hmcts.dts.fact.entity.OpeningTime openingTimeEntity = new OpeningTime();
        openingTimeEntity.setType("opening time type");
        openingTimeEntity.setHours("opening times");
        courtEntity.setOpeningTimes(Collections.singletonList(openingTimeEntity));

        final uk.gov.hmcts.dts.fact.entity.Facility facilityEntity = new Facility();
        facilityEntity.setDescription("<p>Description of facility</p>");
        facilityEntity.setDescriptionCy("<p>Description of facility in Welsh</p>");
        facilityEntity.setName("Facility");
        courtEntity.setFacilities(Collections.singletonList(facilityEntity));

        courtEntity.setInfo("<p>Info on court</p>");
        courtEntity.setInfoCy("<p>Info on court in Welsh</p>");
        courtEntity.setName("Name");
        courtEntity.setSlug("name-slug");
        courtEntity.setNameCy("Name in Welsh");
        courtEntity.setDirections("Directions");
        courtEntity.setDirectionsCy("Directions in Welsh");
        courtEntity.setAlert("Alert");
        courtEntity.setAlertCy("Alert in Welsh");
        courtEntity.setUpdatedAt(currentTime);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }

        CourtReference court = new CourtReference(courtEntity);
        assertEquals(welsh ? "Name in Welsh" : "Name", court.getName());
        assertEquals("name-slug", court.getSlug());
        assertEquals(
            new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(currentTime), court.getUpdatedAt());

        LocaleContextHolder.resetLocaleContext();
    }


}
