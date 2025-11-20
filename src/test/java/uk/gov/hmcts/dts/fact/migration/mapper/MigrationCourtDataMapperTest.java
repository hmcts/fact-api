package uk.gov.hmcts.dts.fact.migration.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLawSpoe;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.entity.CourtDxCode;
import uk.gov.hmcts.dts.fact.entity.CourtLocalAuthorityAreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.DxCode;
import uk.gov.hmcts.dts.fact.entity.Facility;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.entity.LocalAuthority;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt;
import uk.gov.hmcts.dts.fact.migration.model.CourtMigrationData;
import uk.gov.hmcts.dts.fact.migration.model.CourtPhotoData;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawSpoeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtLocalAuthorityAreaOfLawRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MigrationCourtDataMapperTest {

    private static final String IMAGE_BASE = "https://factaat.blob.core.windows.net/images/";

    @Mock
    private CourtAreaOfLawRepository courtAreaOfLawRepository;
    @Mock
    private CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository;
    @Mock
    private CourtLocalAuthorityAreaOfLawRepository courtLocalAuthorityAreaOfLawRepository;

    private MigrationCourtDataMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MigrationCourtDataMapper(courtAreaOfLawRepository, courtAreaOfLawSpoeRepository, courtLocalAuthorityAreaOfLawRepository, IMAGE_BASE);
    }

    @Test
    void shouldMapMinimalCourtData() {
        Court court = new Court();
        court.setId(1);
        court.setName("Test Court");
        court.setSlug("test-court");
        court.setDisplayed(true);
        court.setAlert("notice");

        when(courtAreaOfLawRepository.getCourtAreaOfLawByCourtId(1)).thenReturn(List.of());
        when(courtAreaOfLawSpoeRepository.getAllByCourtId(1)).thenReturn(List.of());

        CourtMigrationData result = mapper.mapCourt(court);

        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("Test Court");
        assertThat(result.getSlug()).isEqualTo("test-court");
        assertThat(result.getOpen()).isTrue();
        assertThat(result.getServiceCentre()).isFalse();
        assertThat(result.getCourtServiceAreas()).isNull();
        assertThat(result.getCourtLocalAuthorities()).isNull();
        assertThat(result.getCourtProfessionalInformation()).isNotNull();
        assertThat(result.getCourtProfessionalInformation().getInterviewRooms()).isFalse();
        assertThat(result.getCourtProfessionalInformation().getVideoHearings()).isFalse();
        assertThat(result.getCourtProfessionalInformation().getCommonPlatform()).isFalse();
        assertThat(result.getCourtProfessionalInformation().getAccessScheme()).isFalse();
        assertThat(result.getCourtCodes()).isNull();
        assertThat(result.getCourtAreasOfLaw()).isNull();
        assertThat(result.getCourtSinglePointsOfEntry()).isNull();
        assertThat(result.getCourtDxCodes()).isNull();
        assertThat(result.getCourtFax()).isNull();
        assertThat(result.getCourtPhoto()).isNull();
    }

    @Test
    void shouldPopulateCourtAssociationsAndNormalisePhotoPath() {
        Court court = new Court();
        court.setId(3);
        court.setName("London Court");
        court.setSlug("london-court");
        court.setDisplayed(true);
        court.setImageFile("/court.jpg");

        ServiceArea serviceArea = new ServiceArea();
        serviceArea.setId(20);
        ServiceAreaCourt serviceAreaCourt = new ServiceAreaCourt();
        serviceAreaCourt.setId(200);
        serviceAreaCourt.setCatchmentType("regional");
        serviceAreaCourt.setServicearea(serviceArea);
        court.setServiceAreaCourts(List.of(serviceAreaCourt));

        InPerson inPerson = new InPerson();
        inPerson.setAccessScheme(true);
        inPerson.setCommonPlatform(true);
        inPerson.setCourtId(court);
        court.setInPerson(inPerson);

        Facility interviewFacility = new Facility();
        interviewFacility.setName("Interview room");
        Facility videoFacility = new Facility();
        videoFacility.setName("Video facilities");
        court.setFacilities(List.of(interviewFacility, videoFacility));

        LocalAuthority localAuthority = new LocalAuthority();
        localAuthority.setId(321);
        AreaOfLaw localAuthorityAreaOfLaw = new AreaOfLaw();
        localAuthorityAreaOfLaw.setId(600);
        CourtLocalAuthorityAreaOfLaw courtLocalAuthorityArea = new CourtLocalAuthorityAreaOfLaw();
        courtLocalAuthorityArea.setId(322);
        courtLocalAuthorityArea.setAreaOfLaw(localAuthorityAreaOfLaw);
        courtLocalAuthorityArea.setCourt(court);
        courtLocalAuthorityArea.setLocalAuthority(localAuthority);

        Contact faxContact = new Contact();
        faxContact.setId(400);
        faxContact.setFax(true);
        faxContact.setNumber("0123456789");
        CourtContact courtContact = new CourtContact();
        courtContact.setContact(faxContact);
        court.setCourtContacts(List.of(courtContact));

        DxCode dxCode = new DxCode();
        dxCode.setId(500);
        dxCode.setCode("DX123");
        dxCode.setExplanation("dx explanation");
        CourtDxCode courtDxCode = new CourtDxCode();
        courtDxCode.setDxCode(dxCode);
        court.setCourtDxCodes(List.of(courtDxCode));

        AreaOfLaw areaOfLaw = new AreaOfLaw();
        areaOfLaw.setId(600);
        CourtAreaOfLaw courtAreaOfLaw = new CourtAreaOfLaw();
        courtAreaOfLaw.setId(601);
        courtAreaOfLaw.setAreaOfLaw(areaOfLaw);
        courtAreaOfLaw.setCourt(court);

        CourtAreaOfLawSpoe courtAreaOfLawSpoe = new CourtAreaOfLawSpoe();
        courtAreaOfLawSpoe.setId(701);
        courtAreaOfLawSpoe.setAreaOfLaw(areaOfLaw);
        courtAreaOfLawSpoe.setCourt(court);

        when(courtAreaOfLawRepository.getCourtAreaOfLawByCourtId(3)).thenReturn(List.of(courtAreaOfLaw));
        when(courtAreaOfLawSpoeRepository.getAllByCourtId(3)).thenReturn(List.of(courtAreaOfLawSpoe));
        when(courtLocalAuthorityAreaOfLawRepository.findByCourtId(3)).thenReturn(List.of(courtLocalAuthorityArea));

        CourtMigrationData result = mapper.mapCourt(court);

        assertThat(result.getCourtServiceAreas()).singleElement()
            .satisfies(area -> {
                assertThat(area.getId()).isEqualTo(200);
                assertThat(area.getCatchmentType()).isEqualTo("regional");
                assertThat(area.getServiceAreaIds()).containsExactly(20);
            });

        assertThat(result.getCourtLocalAuthorities()).singleElement()
            .satisfies(localAuthorityData -> {
                assertThat(localAuthorityData.getAreaOfLawId()).isEqualTo(600);
                assertThat(localAuthorityData.getLocalAuthorityIds()).containsExactly(321);
            });

        assertThat(result.getCourtProfessionalInformation()).isNotNull();
        assertThat(result.getCourtProfessionalInformation().getInterviewRooms()).isTrue();
        assertThat(result.getCourtProfessionalInformation().getVideoHearings()).isTrue();
        assertThat(result.getCourtProfessionalInformation().getAccessScheme()).isTrue();
        assertThat(result.getCourtProfessionalInformation().getCommonPlatform()).isTrue();

        assertThat(result.getCourtFax()).singleElement()
            .satisfies(fax -> {
                assertThat(fax.getId()).isEqualTo("400");
                assertThat(fax.getFaxNumber()).isEqualTo("0123456789");
            });

        assertThat(result.getCourtDxCodes()).singleElement()
            .satisfies(code -> {
                assertThat(code.getId()).isEqualTo("500");
                assertThat(code.getDxCode()).isEqualTo("DX123");
                assertThat(code.getExplanation()).isEqualTo("dx explanation");
            });

        assertThat(result.getCourtAreasOfLaw())
            .satisfies(areas -> {
                assertThat(areas.getId()).isEqualTo("601");
                assertThat(areas.getAreasOfLaw()).containsExactly(600);
            });

        assertThat(result.getCourtSinglePointsOfEntry())
            .satisfies(spoe -> {
                assertThat(spoe.getId()).isEqualTo("701");
                assertThat(spoe.getAreasOfLaw()).containsExactly(600);
            });

        CourtPhotoData photo = result.getCourtPhoto();
        assertThat(photo).isNotNull();
        assertThat(photo.getImagePath()).isEqualTo("https://factaat.blob.core.windows.net/images/court.jpg");
    }

    @Test
    void shouldThrowWhenCourtIsNull() {
        assertThatThrownBy(() -> mapper.mapCourt(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("court must not be null");
    }
}
