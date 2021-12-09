package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtForDownload;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@SuppressWarnings("PMD.TooManyMethods")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AdminService.class)
class AdminServiceTest {

    private Court courtEntity;
    private static final String SOME_COURT = "some court";
    private static final String SOME_SLUG = "some-slug";
    private static final Double LATITUDE = 1.0;
    private static final Double LONGITUDE = -3.0;
    private static final String IMAGE_FILE = "some-image.jpeg";
    private static uk.gov.hmcts.dts.fact.model.admin.Court court;
    private static final String NOT_FOUND = "Not found: ";

    @Autowired
    private AdminService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private RolesProvider rolesProvider;

    @MockBean
    private AdminAuditService adminAuditService;

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
            emptyList()
        );
    }

    @Test
    void shouldReturnAllCourts() {
        final Court mock = mock(Court.class);
        when(courtRepository.findAll()).thenReturn(singletonList(mock));
        final List<CourtReference> results = adminService.getAllCourtReferences();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isInstanceOf(CourtReference.class);
    }

    @Test
    void shouldReturnAllCourtsForDownload() {
        final Court mock = mock(Court.class);
        when(courtRepository.findAll()).thenReturn(singletonList(mock));
        final List<CourtForDownload> results = adminService.getAllCourtsForDownload();
        assertThat(results.size()).isEqualTo(1);
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
                                                           courtResults, SOME_SLUG);
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
        assertThat(courtResults.getAccessScheme()).isEqualTo(null);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court details",
                                                           court,
                                                           courtResults, SOME_SLUG);
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
                                                           courtResults, SOME_SLUG);
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
                                            any(), anyString());
    }

    @Test
    void shouldNotDeleteCourtIfNoCourtExists() {
        String notFoundSlugError = "a slug error";
        when(courtRepository.findBySlug(SOME_SLUG)).thenThrow(new NotFoundException(notFoundSlugError));
        assertThatThrownBy(() -> adminService.deleteCourt(SOME_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + notFoundSlugError);
    }

    @Test
    void shouldAddNewCourtSuccessInPerson() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.empty());
        when(courtRepository.save(any(Court.class))).thenAnswer(i -> i.getArguments()[0]);
        uk.gov.hmcts.dts.fact.model.admin.Court returnedCourt =
            adminService.addNewCourt(SOME_COURT, SOME_SLUG, false);
        assertThat(returnedCourt.getName()).isEqualTo(SOME_COURT);
        assertThat(returnedCourt.getSlug()).isEqualTo(SOME_SLUG);
        assertThat(returnedCourt.getOpen()).isTrue();
        assertThat(returnedCourt.getInPerson()).isTrue();
        verify(adminAuditService).saveAudit("Create new court", null,
                                            returnedCourt, SOME_SLUG);
        verify(courtRepository, times(2)).save(any(Court.class));
    }

    @Test
    void shouldAddNewCourtSuccessNotInPerson() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.empty());
        when(courtRepository.save(any(Court.class))).thenAnswer(i -> i.getArguments()[0]);
        uk.gov.hmcts.dts.fact.model.admin.Court returnedCourt =
            adminService.addNewCourt(SOME_COURT, SOME_SLUG, true);
        assertThat(returnedCourt.getName()).isEqualTo(SOME_COURT);
        assertThat(returnedCourt.getSlug()).isEqualTo(SOME_SLUG);
        assertThat(returnedCourt.getOpen()).isTrue();
        assertThat(returnedCourt.getInPerson()).isFalse();
        verify(adminAuditService).saveAudit("Create new court", null,
                                            returnedCourt, SOME_SLUG);
        verify(courtRepository, times(2)).save(any(Court.class));
    }

    @Test
    void shouldNotAddNewCourtIfAlreadyExists() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(new Court()));
        assertThatThrownBy(() -> adminService.addNewCourt(SOME_COURT, SOME_SLUG, false))
            .isInstanceOf(DuplicatedListItemException.class)
            .hasMessage("Court already exists with slug: " + SOME_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(),
                                                     anyString(), anyString());
        verify(courtRepository, never()).save(any(Court.class));
    }
}
