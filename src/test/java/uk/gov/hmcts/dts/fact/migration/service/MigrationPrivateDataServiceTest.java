package uk.gov.hmcts.dts.fact.migration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.ContactType;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLawSpoe;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.entity.CourtDxCode;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.DxCode;
import uk.gov.hmcts.dts.fact.entity.LocalAuthority;
import uk.gov.hmcts.dts.fact.entity.OpeningType;
import uk.gov.hmcts.dts.fact.entity.Region;
import uk.gov.hmcts.dts.fact.entity.Service;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt;
import uk.gov.hmcts.dts.fact.entity.ServiceCentre;
import uk.gov.hmcts.dts.fact.migration.mapper.MigrationCourtDataMapper;
import uk.gov.hmcts.dts.fact.migration.model.CourtAreasOfLawData;
import uk.gov.hmcts.dts.fact.migration.model.CourtCodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtDxCodeData;
import uk.gov.hmcts.dts.fact.migration.model.CourtFaxData;
import uk.gov.hmcts.dts.fact.migration.model.CourtMigrationData;
import uk.gov.hmcts.dts.fact.migration.model.CourtPhotoData;
import uk.gov.hmcts.dts.fact.migration.model.CourtServiceAreaData;
import uk.gov.hmcts.dts.fact.migration.model.CourtSinglePointOfEntryData;
import uk.gov.hmcts.dts.fact.migration.model.MigrationExportResponse;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.ContactTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawSpoeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.LocalAuthorityRepository;
import uk.gov.hmcts.dts.fact.repositories.OpeningTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.RegionRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MigrationPrivateDataServiceTest {

    @Mock
    private CourtRepository courtRepository;
    @Mock
    private LocalAuthorityRepository localAuthorityRepository;
    @Mock
    private ServiceAreaRepository serviceAreaRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private ContactTypeRepository contactTypeRepository;
    @Mock
    private OpeningTypeRepository openingTypeRepository;
    @Mock
    private CourtTypeRepository courtTypeRepository;
    @Mock
    private RegionRepository regionRepository;
    @Mock
    private AreasOfLawRepository areasOfLawRepository;
    @Mock
    private CourtAreaOfLawRepository courtAreaOfLawRepository;
    @Mock
    private CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository;

    private MigrationPrivateDataService migrationPrivateDataService;

    private Court court;

    @BeforeEach
    void setUp() {
        migrationPrivateDataService = new MigrationPrivateDataService(
            courtRepository,
            localAuthorityRepository,
            serviceAreaRepository,
            serviceRepository,
            contactTypeRepository,
            openingTypeRepository,
            courtTypeRepository,
            regionRepository,
            areasOfLawRepository,
            new MigrationCourtDataMapper(
                courtAreaOfLawRepository,
                courtAreaOfLawSpoeRepository,
                "https://factaat.blob.core.windows.net/images"
            )
        );
        court = new Court();
        court.setId(12);
        court.setName("Test Court");
        court.setSlug("test-slug");
        court.setAlert("urgent notice");
        court.setDisplayed(Boolean.TRUE);
        court.setRegionId(9);
        court.setServiceCentre(new ServiceCentre());
        court.setNumber(1111);
        court.setCciCode(2222);
        court.setMagistrateCode(3333);
        court.setCourtCode(4444);
        court.setLocationCode(5555);
        court.setGbs("GBS123");
        court.setImageFile("court.jpg");
    }

    @Test
    void shouldReturnCourtMigrationDataForDisplayedCourtsOnly() {
        Court anotherCourt = new Court();
        anotherCourt.setId(34);
        anotherCourt.setName("Another Court");
        anotherCourt.setSlug("another-slug");
        anotherCourt.setAlert("notice");
        anotherCourt.setDisplayed(Boolean.FALSE);
        anotherCourt.setRegionId(5);

        final List<LocalAuthority> localAuthorities = List.of(new LocalAuthority(1, "Authority"));
        ServiceArea serviceArea = new ServiceArea();
        serviceArea.setId(2);
        serviceArea.setName("Service Area");
        serviceArea.setNameCy("Service Area Cy");
        serviceArea.setDescription("Description");
        serviceArea.setDescriptionCy("Description Cy");
        serviceArea.setSlug("service-area");
        serviceArea.setOnlineUrl("http://example.com");
        serviceArea.setOnlineText("Online text");
        serviceArea.setOnlineTextCy("Online text cy");
        serviceArea.setType("type");
        serviceArea.setText("Text");
        serviceArea.setTextCy("Text Cy");
        serviceArea.setCatchmentMethod("catchment");
        AreaOfLaw areaOfLaw = new AreaOfLaw();
        areaOfLaw.setId(6);
        serviceArea.setAreaOfLaw(areaOfLaw);

        Service service = new Service();
        service.setId(3);
        service.setName("Service");
        service.setNameCy("Service Cy");
        service.setDescription("Service desc");
        service.setDescriptionCy("Service desc cy");
        service.setSlug("service");

        ServiceArea secondServiceArea = new ServiceArea();
        secondServiceArea.setId(3);
        secondServiceArea.setName("Service Area 2");
        secondServiceArea.setCatchmentMethod("catchment");
        secondServiceArea.setAreaOfLaw(areaOfLaw);

        final List<ServiceArea> serviceAreas = List.of(serviceArea);

        ContactType contactType = new ContactType();
        contactType.setId(4);
        contactType.setDescription("Phone");
        contactType.setDescriptionCy("Phone cy");
        final List<ContactType> contactTypes = List.of(contactType);

        OpeningType openingType = new OpeningType();
        openingType.setId(5);
        openingType.setDescription("Opening");
        openingType.setDescriptionCy("Opening cy");
        final List<OpeningType> openingTypes = List.of(openingType);

        CourtType courtType = new CourtType();
        courtType.setId(7);
        courtType.setName("Court Type");
        courtType.setSearch("court-type");
        final List<CourtType> courtTypes = List.of(courtType);

        final List<Region> regions = List.of(new Region(8, "Region", "England"));

        AreaOfLaw areaOfLawType = new AreaOfLaw();
        areaOfLawType.setId(9);
        areaOfLawType.setName("Area of law");
        areaOfLawType.setExternalLink("http://external");
        areaOfLawType.setExternalLinkCy("http://external-cy");
        areaOfLawType.setExternalLinkDescription("External desc");
        areaOfLawType.setExternalLinkDescriptionCy("External desc cy");
        areaOfLawType.setAltName("Alt name");
        areaOfLawType.setAltNameCy("Alt name cy");
        areaOfLawType.setDisplayName("Display");
        areaOfLawType.setDisplayNameCy("Display cy");
        areaOfLawType.setDisplayExternalLink("Y");
        final List<AreaOfLaw> areaOfLawTypes = List.of(areaOfLawType);

        CourtAreaOfLaw courtAreaOfLaw = new CourtAreaOfLaw();
        courtAreaOfLaw.setId(70);
        courtAreaOfLaw.setAreaOfLaw(areaOfLawType);
        courtAreaOfLaw.setCourt(court);
        final List<CourtAreaOfLaw> courtAreasOfLaw = List.of(courtAreaOfLaw);

        CourtAreaOfLawSpoe courtAreaOfLawSpoe = new CourtAreaOfLawSpoe();
        courtAreaOfLawSpoe.setId(80);
        courtAreaOfLawSpoe.setAreaOfLaw(areaOfLawType);
        courtAreaOfLawSpoe.setCourt(court);
        final List<CourtAreaOfLawSpoe> courtAreasOfLawSpoe = List.of(courtAreaOfLawSpoe);

        ServiceAreaCourt serviceAreaCourt = new ServiceAreaCourt();
        serviceAreaCourt.setId(50);
        serviceAreaCourt.setCatchmentType("regional");
        serviceAreaCourt.setServicearea(serviceArea);
        serviceAreaCourt.setCourt(court);

        ServiceAreaCourt secondServiceAreaCourt = new ServiceAreaCourt();
        secondServiceAreaCourt.setId(51);
        secondServiceAreaCourt.setCatchmentType("regional");
        secondServiceAreaCourt.setServicearea(secondServiceArea);
        secondServiceAreaCourt.setCourt(court);

        court.setServiceAreaCourts(List.of(serviceAreaCourt, secondServiceAreaCourt));

        CourtPostcode postcodeOne = new CourtPostcode();
        postcodeOne.setId(100);
        postcodeOne.setPostcode("AB1 2CD");
        postcodeOne.setCourt(court);

        CourtPostcode postcodeTwo = new CourtPostcode();
        postcodeTwo.setId(101);
        postcodeTwo.setPostcode("EF3 4GH");
        postcodeTwo.setCourt(court);

        court.setCourtPostcodes(List.of(postcodeOne, postcodeTwo));

        Contact faxContact = new Contact();
        faxContact.setId(90);
        faxContact.setNumber("0123456789");
        faxContact.setFax(true);
        CourtContact courtContact = new CourtContact();
        courtContact.setContact(faxContact);
        courtContact.setCourt(court);
        court.setCourtContacts(List.of(courtContact));

        DxCode dxCode = new DxCode();
        dxCode.setId(120);
        dxCode.setCode("DX123");
        dxCode.setExplanation("DX explanation");
        CourtDxCode courtDxCode = new CourtDxCode();
        courtDxCode.setDxCode(dxCode);
        courtDxCode.setCourt(court);
        court.setCourtDxCodes(List.of(courtDxCode));

        when(courtRepository.findAll()).thenReturn(List.of(court, anotherCourt));
        when(localAuthorityRepository.findAll()).thenReturn(localAuthorities);
        when(serviceAreaRepository.findAll()).thenReturn(serviceAreas);
        when(serviceRepository.findAll()).thenReturn(List.of(service));
        when(contactTypeRepository.findAll()).thenReturn(contactTypes);
        when(openingTypeRepository.findAll()).thenReturn(openingTypes);
        when(courtTypeRepository.findAll()).thenReturn(courtTypes);
        when(regionRepository.findAll()).thenReturn(regions);
        when(areasOfLawRepository.findAll()).thenReturn(areaOfLawTypes);
        when(courtAreaOfLawRepository.getCourtAreaOfLawByCourtId(12)).thenReturn(courtAreasOfLaw);
        when(courtAreaOfLawSpoeRepository.getAllByCourtId(12)).thenReturn(courtAreasOfLawSpoe);

        MigrationExportResponse response = migrationPrivateDataService.getCourtExport();

        assertThat(response.getCourts()).hasSize(1);
        CourtMigrationData first = response.getCourts().get(0);
        assertThat(first.getId()).isEqualTo("12");
        assertThat(first.getSlug()).isEqualTo("test-slug");
        assertThat(first.getOpen()).isTrue();
        assertThat(first.getRegionId()).isEqualTo(9);
        assertThat(first.getServiceCentre()).isTrue();
        assertThat(first.getCourtServiceAreas()).hasSize(1);
        CourtServiceAreaData courtServiceAreaData = first.getCourtServiceAreas().get(0);
        assertThat(courtServiceAreaData.getServiceAreaIds()).containsExactlyInAnyOrder(2, 3);
        assertThat(courtServiceAreaData.getCatchmentType()).isEqualTo("regional");
        assertThat(courtServiceAreaData.getId()).isEqualTo(50);

        assertThat(first.getCourtPostcodes()).hasSize(2);
        assertThat(first.getCourtPostcodes().get(0).getId()).isEqualTo(100);
        assertThat(first.getCourtPostcodes().get(0).getPostcode()).isEqualTo("AB1 2CD");
        assertThat(first.getCourtPostcodes().get(1).getId()).isEqualTo(101);
        assertThat(first.getCourtPostcodes().get(1).getPostcode()).isEqualTo("EF3 4GH");

        CourtCodeData courtCodes = first.getCourtCodes();
        assertThat(courtCodes).isNotNull();
        assertThat(courtCodes.getCrownCourtCode()).isEqualTo(1111);
        assertThat(courtCodes.getCountyCourtCode()).isEqualTo(2222);
        assertThat(courtCodes.getMagistrateCourtCode()).isEqualTo(3333);
        assertThat(courtCodes.getFamilyCourtCode()).isEqualTo(4444);
        assertThat(courtCodes.getTribunalCode()).isEqualTo(5555);
        assertThat(courtCodes.getGbs()).isEqualTo("GBS123");

        CourtAreasOfLawData courtAreasOfLawData = first.getCourtAreasOfLaw();
        assertThat(courtAreasOfLawData).isNotNull();
        assertThat(courtAreasOfLawData.getId()).isEqualTo("70");
        assertThat(courtAreasOfLawData.getAreasOfLaw()).containsExactly(9);

        CourtSinglePointOfEntryData spoe = first.getCourtSinglePointsOfEntry();
        assertThat(spoe).isNotNull();
        assertThat(spoe.getId()).isEqualTo("80");
        assertThat(spoe.getAreasOfLaw()).containsExactly(9);

        List<CourtDxCodeData> dxCodes = first.getCourtDxCodes();
        assertThat(dxCodes).hasSize(1);
        CourtDxCodeData dxData = dxCodes.get(0);
        assertThat(dxData.getId()).isEqualTo("120");
        assertThat(dxData.getDxCode()).isEqualTo("DX123");
        assertThat(dxData.getExplanation()).isEqualTo("DX explanation");

        List<CourtFaxData> faxData = first.getCourtFax();
        assertThat(faxData).hasSize(1);
        CourtFaxData fax = faxData.get(0);
        assertThat(fax.getId()).isEqualTo("90");
        assertThat(fax.getFaxNumber()).isEqualTo("0123456789");

        CourtPhotoData photo = first.getCourtPhoto();
        assertThat(photo).isNotNull();
        assertThat(photo.getImagePath()).isEqualTo("https://factaat.blob.core.windows.net/images/court.jpg");

        assertThat(response.getLocalAuthorityTypes()).hasSize(1);
        assertThat(response.getLocalAuthorityTypes().get(0).getName()).isEqualTo("Authority");

        assertThat(response.getServiceAreas()).hasSize(1);
        assertThat(response.getServiceAreas().get(0).getName()).isEqualTo("Service Area");
        assertThat(response.getServiceAreas().get(0).getAreaOfLawId()).isEqualTo(6);

        assertThat(response.getServices()).hasSize(1);
        assertThat(response.getServices().get(0).getName()).isEqualTo("Service");

        assertThat(response.getContactDescriptionTypes()).hasSize(1);
        assertThat(response.getContactDescriptionTypes().get(0).getDescription()).isEqualTo("Phone");

        assertThat(response.getOpeningHourTypes()).hasSize(1);
        assertThat(response.getOpeningHourTypes().get(0).getDescription()).isEqualTo("Opening");

        assertThat(response.getCourtTypes()).hasSize(1);
        assertThat(response.getCourtTypes().get(0).getName()).isEqualTo("Court Type");

        assertThat(response.getRegions()).hasSize(1);
        assertThat(response.getRegions().get(0).getName()).isEqualTo("Region");

        assertThat(response.getAreaOfLawTypes()).hasSize(1);
        assertThat(response.getAreaOfLawTypes().get(0).getName()).isEqualTo("Area of law");
    }
}
