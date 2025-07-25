package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtForDownload;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtHistoryRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtLocalAuthorityAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports"})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AdminService.class)
class AdminServiceTest {

    private Court courtEntity;
    private List<ServiceArea> serviceAreaList;
    private List<AreaOfLaw> areaOfLawList;
    private static final String SOME_COURT = "some court";
    private static final String SOME_SLUG = "some-slug";
    private static final Double LATITUDE = 1.0;
    private static final Double LONGITUDE = -3.0;
    private static final String IMAGE_FILE = "some-image.jpeg";
    private static uk.gov.hmcts.dts.fact.model.admin.Court court;
    private static final String NOT_FOUND = "Not found: ";
    private static final String AUDIT_TYPE = "Create new court";
    private static final List<String> SERVICE_AREAS = Arrays.asList("Benefits", "Tax", "Money claims");
    private static final String REGION = "North West";

    @Autowired
    private AdminService adminService;

    @MockitoBean
    private CourtRepository courtRepository;

    @MockitoBean
    private ServiceAreaRepository serviceAreaRepository;

    @MockitoBean
    private AreasOfLawRepository areasOfLawRepository;

    @MockitoBean
    private RolesProvider rolesProvider;

    @MockitoBean
    private CourtHistoryRepository courtHistoryRepository;

    @MockitoBean
    private AdminAuditService adminAuditService;

    @MockitoBean
    private CourtLocalAuthorityAreaOfLawRepository courtLocalAuthorityAreaOfLawRepository;

    @BeforeEach
    void setUp() {
        courtEntity = new Court();
        courtEntity.setId(100);
        courtEntity.setSlug("slug");
        courtEntity.setName("some-name");
        courtEntity.setNameCy("some-name-cy");
        courtEntity.setInfo("some-info");
        courtEntity.setInfoCy("some-info-cy");
        courtEntity.setImageFile("some-image.jpeg");
        courtEntity.setAlert("some-urgent-message");
        courtEntity.setAlertCy("some-urgent-message-cy");
        courtEntity.setDisplayed(true);
        when(serviceAreaRepository.findAllByNameIn(any())).thenReturn(Optional.empty());
        when(areasOfLawRepository.findAllByNameIn(any())).thenReturn(Optional.empty());

        court = new uk.gov.hmcts.dts.fact.model.admin.Court(
            "slug",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre Info",
            "Birmingham Civil and Family Justice Centre Info",
            true,
            true,
            true,
            "Birmingham Civil and Family Justice Centre Alert",
            "Birmingham Civil and Family Justice Centre Alert",
            emptyList(),
            emptyList(),
            false
        );

        serviceAreaList = new ArrayList<>();
        areaOfLawList = new ArrayList<>();


        serviceAreaList.add(new ServiceArea(1, "Money claims", "Hawliadau am arian",
                                            "Claims for when you are owed money or responding to money claims against you.",
                                            "Hawliadau pan mae arian yn ddyledus ichi neu ymateb i hawliadau am arian yn eich erbyn.",
                                            "money-claims",  "https://www.gov.uk/make-money-claim",
                                            "Make a money claim online", "Gwneud hawliad am arian ar-lein",
                                            "civil", "We manage your money claims applications at our central service centre. Find where to send your documents or ask about your application’s progress.",
                                            "Rydym yn rheoli eich ceisiadau am hawliadau am arian yn ein canolfan wasanaeth ganolog",
                                            "postcode"
        ));

        serviceAreaList.add(new ServiceArea(7, "Tax", "Treth", "Appealing a tax decision.",
                                            "Apelio yn erbyn penderfyniad treth.", "tax",  null,
                                            null,  null, "other", null, null,
                                            "proximity"
        ));

        serviceAreaList.add(new ServiceArea(5, "Benefits", "Budd-daliadau",
                                            "Appealing entitlement to benefits such as Personal Independence Payment (PIP), Employment and Support Allowance (ESA) or Universal Credit.",
                                            "Apelio yn erbyn hawl i gael budd-daliadau fel Taliad Annibyniaeth Personol (PIP), Lwfans Cyflogaeth a Chymorth (ESA) neu Gredyd Cynhwysol",
                                            "benefits-slug",  "https://www.gov.uk/appeal-benefit-decision/submit-appeal",
                                            "Appeal a benefits decision online", "Apelio yn erbyn penderfyniad budd-daliadau ar-lein",
                                            "other", "We manage benefits appeals at our central service centre. Find where to send your documents or ask about your application’s progress.",
                                            "Rydym yn rheoli apeliadau budd-daliadau yn ein canolfan wasanaeth ganolog",
                                            "proximity"
        ));

        areaOfLawList.add(new AreaOfLaw(34_252, "Social security", "https://www.gov.uk/tribunal/sscs",
                                        "https://www.gov.uk/tribunal/sscs",
                                        "Information about the Social Security and Child Support Tribunal",
                                        "Gwybodaeth ynglŷn â'r tribiwnlys nawdd cymdeithasol a chynnal plant",
                                        "Benefits", "Budd-daliadau", "Social security and Benefits", "Budd-daliadau",
                                        "https://www.gov.uk/appeal-benefit-decision"));

        areaOfLawList.add(new AreaOfLaw(34_263, "Tax", "https://www.gov.uk/tax-tribunal", "https://www.gov.uk/tax-tribunal",
                                        "Information about tax tribunals", "Gwybodaeth am tribiwnlysoedd treth", null,
                                        "Treth", null, "Treth", null));

        areaOfLawList.add(new AreaOfLaw(34_254, "Money claims", "https://www.gov.uk/make-court-claim-for-money", "https://www.gov.uk/make-court-claim-for-money", "Information about making a court claim for money", "Gwybodaeth ynglŷn â gwneud hawliad llys am arian", null, "Hawliadau am arian", null, "Hawliadau am arian", null
        ));
    }

    @Test
    void shouldReturnAllCourts() {
        final Court mock = mock(Court.class);
        when(courtRepository.findAll()).thenReturn(singletonList(mock));
        final List<CourtReference> results = adminService.getAllCourtReferences();
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isInstanceOf(CourtReference.class);
    }

    @Test
    void shouldReturnAllCourtsForDownload() {
        final Court mock = mock(Court.class);
        when(courtRepository.findAll()).thenReturn(singletonList(mock));
        final List<CourtForDownload> results = adminService.getAllCourtsForDownload();
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isInstanceOf(CourtForDownload.class);
    }

    @Test
    void shouldReturnCourtObject() {
        final Court mock = mock(Court.class);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(mock));
        assertThat(adminService.getCourtBySlug(SOME_SLUG)).isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.Court.class);
    }

    @Test
    void shouldReturnCourtEntityObject() {
        final Court mock = mock(Court.class);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(mock));
        assertThat(adminService.getCourtEntityBySlug(SOME_SLUG)).isInstanceOf(Court.class);
    }

    @Test
    void shouldSaveCourtAsAdmin() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(courtEntity));
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-admin"));
        when(courtRepository.save(courtEntity)).thenReturn(courtEntity);
        final uk.gov.hmcts.dts.fact.model.admin.Court courtResults = adminService.save(SOME_SLUG, court);
        assertThat(courtResults.getAlert()).isEqualTo(court.getAlert());
        assertThat(courtResults.getAlertCy()).isEqualTo(court.getAlertCy());
        assertThat(courtResults.getInfo()).isNotEqualTo(court.getInfo());
        assertThat(courtResults.getInfoCy()).isNotEqualTo(court.getInfoCy());
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court details",
                                                           court,
                                                           courtResults, SOME_SLUG
        );
    }

    @Test
    void shouldSaveCourtAsSuperAdmin() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(courtEntity));
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-super-admin"));
        when(courtRepository.save(courtEntity)).thenReturn(courtEntity);
        final uk.gov.hmcts.dts.fact.model.admin.Court courtResults = adminService.save(SOME_SLUG, court);
        assertThat(courtResults.getAlert()).isEqualTo(court.getAlert());
        assertThat(courtResults.getAlertCy()).isEqualTo(court.getAlertCy());
        assertThat(courtResults.getInfo()).isEqualTo(court.getInfo());
        assertThat(courtResults.getInfoCy()).isEqualTo(court.getInfoCy());
        assertThat(courtResults.getOpen()).isEqualTo(court.getOpen());
        assertThat(courtResults.getAccessScheme()).isNull();
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court details",
                                                           court,
                                                           courtResults, SOME_SLUG
        );
    }

    @Test
    void shouldSaveCourtAsSuperAdminAndUpdateAccessScheme() {
        InPerson inPerson = new InPerson();
        inPerson.setIsInPerson(true);
        inPerson.setAccessScheme(true);
        courtEntity.setInPerson(inPerson);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(courtEntity));
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-super-admin"));
        when(courtRepository.save(courtEntity)).thenReturn(courtEntity);
        final uk.gov.hmcts.dts.fact.model.admin.Court courtResults = adminService.save(SOME_SLUG, court);
        assertThat(courtResults.getAccessScheme()).isEqualTo(court.getAccessScheme());
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court details",
                                                           court,
                                                           courtResults, SOME_SLUG
        );
    }

    @Test
    void shouldUpdateAllCourtInfo() {
        CourtInfoUpdate info = new CourtInfoUpdate(singletonList(courtEntity.getSlug()), "updated info", "Welsh info");
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-super-admin"));

        adminService.updateMultipleCourtsInfo(info);
        verify(courtRepository).updateInfoForSlugs(info.getCourts(), info.getInfo(), info.getInfoCy());
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldUpdateCourtLatLon() {
        adminService.updateCourtLatLon(SOME_SLUG, LATITUDE, LONGITUDE);
        verify(courtRepository).updateLatLonBySlug(SOME_SLUG, LATITUDE, LONGITUDE);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldUpdateCourtRegion() {
        adminService.updateCourtRegion(SOME_SLUG, REGION);
        verify(courtRepository).updateRegionBySlug(SOME_SLUG, REGION);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturnCourtImage() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(courtEntity));

        String fileName = adminService.getCourtImage(SOME_SLUG);

        verify(courtRepository, atMostOnce()).findBySlug(SOME_SLUG);
        assertThat(fileName).isEqualTo(IMAGE_FILE);
    }

    @Test
    void shouldNotReturnImageIfCourtNotFound() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getCourtImage(SOME_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + SOME_SLUG);
    }

    @Test
    void shouldUpdateCourtImage() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(courtEntity));
        when(courtRepository.updateCourtImageBySlug(SOME_SLUG, IMAGE_FILE)).thenReturn(IMAGE_FILE);

        String fileName = adminService.updateCourtImage(SOME_SLUG, IMAGE_FILE);

        verify(courtRepository, atMostOnce()).findBySlug(SOME_SLUG);
        verify(courtRepository, atMostOnce()).updateCourtImageBySlug(SOME_SLUG, IMAGE_FILE);

        assertThat(fileName).isEqualTo(IMAGE_FILE);
    }

    @Test
    void shouldNotUpdateImageIfCourtNotFound() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminService.updateCourtImage(SOME_SLUG, IMAGE_FILE))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + SOME_SLUG);
    }

    @Test
    void shouldDeleteCourtIfSlugExists() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(courtEntity));
        assertDoesNotThrow(() -> adminService.deleteCourt(SOME_SLUG));
        verify(courtRepository).findBySlug(SOME_SLUG);
        verify(courtRepository).deleteById(courtEntity.getId());
        verify(adminAuditService).saveAudit(anyString(),
                                            any(uk.gov.hmcts.dts.fact.model.admin.Court.class),
                                            any(), anyString()
        );
        verify(courtHistoryRepository).deleteCourtHistoriesBySearchCourtId(courtEntity.getId());
        verify(courtLocalAuthorityAreaOfLawRepository).deleteByCourtId(courtEntity.getId());
    }

    @Test
    void shouldNotDeleteCourtIfNoCourtExists() {
        String notFoundSlugError = "a slug error";
        when(courtRepository.findBySlug(SOME_SLUG)).thenThrow(new NotFoundException(notFoundSlugError));
        assertThatThrownBy(() -> adminService.deleteCourt(SOME_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + notFoundSlugError);
        verify(courtHistoryRepository, never()).deleteCourtHistoriesBySearchCourtId(any());
        verify(courtLocalAuthorityAreaOfLawRepository, never()).deleteByCourtId(any());
    }

    @Test
    void shouldAddNewCourtSuccessInPerson() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.empty());
        when(courtRepository.save(any(Court.class))).thenAnswer(i -> i.getArguments()[0]);
        uk.gov.hmcts.dts.fact.model.admin.Court returnedCourt =
            adminService.addNewCourt(SOME_COURT, SOME_SLUG, false,
                                     LONGITUDE, LATITUDE, new ArrayList<>()
            );
        assertThat(returnedCourt.getName()).isEqualTo(SOME_COURT);
        assertThat(returnedCourt.getSlug()).isEqualTo(SOME_SLUG);
        assertThat(returnedCourt.getOpen()).isTrue();
        assertThat(returnedCourt.getInPerson()).isTrue();
        verify(adminAuditService).saveAudit(AUDIT_TYPE, null,
                                            returnedCourt, SOME_SLUG
        );
        verify(courtRepository, times(2)).save(any(Court.class));
    }

    @Test
    void shouldAddNewCourtSuccessNotInPerson() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.empty());
        when(courtRepository.save(any(Court.class))).thenAnswer(i -> i.getArguments()[0]);
        uk.gov.hmcts.dts.fact.model.admin.Court returnedCourt =
            adminService.addNewCourt(SOME_COURT, SOME_SLUG, true,
                                     LONGITUDE, LATITUDE, new ArrayList<>()
            );
        assertThat(returnedCourt.getName()).isEqualTo(SOME_COURT);
        assertThat(returnedCourt.getSlug()).isEqualTo(SOME_SLUG);
        assertThat(returnedCourt.getOpen()).isTrue();
        assertThat(returnedCourt.getInPerson()).isFalse();
        verify(adminAuditService).saveAudit(AUDIT_TYPE, null,
                                            returnedCourt, SOME_SLUG
        );
        verify(courtRepository, times(2)).save(any(Court.class));
    }

    @Test
    void shouldNotAddNewCourtIfAlreadyExists() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(new Court()));
        assertThatThrownBy(() -> adminService.addNewCourt(SOME_COURT, SOME_SLUG, false,
                                                          LONGITUDE, LATITUDE, new ArrayList<>()
        ))
            .isInstanceOf(DuplicatedListItemException.class)
            .hasMessage("Court already exists with slug: " + SOME_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(),
                                                     anyString(), anyString()
        );
        verify(courtRepository, never()).save(any(Court.class));
    }

    @Test
    void shouldSetServiceCentreIntroParagraphs() {
        when(serviceAreaRepository.findAllByNameIn(any()))
            .thenReturn(Optional.ofNullable(serviceAreaList));
        when(areasOfLawRepository.findAllByNameIn(any()))
            .thenReturn(Optional.ofNullable(areaOfLawList));
        when(courtRepository.save(any()))
            .thenReturn(courtEntity);

        ArgumentCaptor<Court> courtArgumentCaptor = ArgumentCaptor.forClass(Court.class);

        adminService.addNewCourt(SOME_COURT, SOME_SLUG, true,
                                 LONGITUDE, LATITUDE, SERVICE_AREAS);

        verify(courtRepository, times(2)).save(courtArgumentCaptor.capture());

        Court entityToCheck = courtArgumentCaptor.getAllValues().get(0);
        assertThat(entityToCheck.getServiceCentre().getIntroParagraph()).isEqualTo("This location services all of England"
                                                                                       + " and Wales for benefits, tax and money claims."
                                                                                       + " We do not provide an in-person service.");
        assertThat(entityToCheck.getServiceCentre().getIntroParagraphCy()).isEqualTo("Mae’r lleoliad hwn yn gwasanaethu Cymru a Lloegr "
                                                                                         + "i gyd ar gyfer hawliadau am arian, treth a "
                                                                                         + "budd-daliadau. Nid ydym yn darparu gwasanaeth "
                                                                                         + "wyneb yn wyneb.");

    }
}
