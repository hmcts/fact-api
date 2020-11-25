package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AdminService.class)
class AdminServiceTest {

    private static final Court COURT = new Court();
    private static final String SOME_SLUG = "some-slug";
    private static CourtGeneral courtGeneral = new CourtGeneral();

    @Autowired
    private AdminService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private RolesProvider rolesProvider;

    @BeforeEach
    void setUp() {
        COURT.setSlug("slug");
        COURT.setName("some-name");
        COURT.setNameCy("some-name-cy");
        COURT.setInfo("some-info");
        COURT.setInfoCy("some-info-cy");
        COURT.setAlert("some-urgent-message");
        COURT.setAlertCy("some-urgent-message-cy");
        COURT.setDisplayed(true);

        courtGeneral = new CourtGeneral(
            "slug",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre Info",
            "Birmingham Civil and Family Justice Centre Info",
            true,
            "Birmingham Civil and Family Justice Centre Alert",
            "Birmingham Civil and Family Justice Centre Alert"
        );
    }

    @Test
    void shouldReturnAllCourts() {
        final Court mock = mock(Court.class);
        when(courtRepository.findAll()).thenReturn(singletonList(mock));
        final List<CourtReference> results = adminService.getAllCourts();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isInstanceOf(CourtReference.class);
    }

    @Test
    void shouldReturnCourtGeneralObject() {
        final Court mock = mock(Court.class);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(mock));
        assertThat(adminService.getCourtGeneralBySlug(SOME_SLUG)).isInstanceOf(CourtGeneral.class);
    }

    @Test
    void shouldReturnCourtEntityObject() {
        final Court mock = mock(Court.class);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(mock));
        assertThat(adminService.getCourtEntityBySlug(SOME_SLUG)).isInstanceOf(Court.class);
    }

    @Test
    void shouldSaveCourtAsAdmin() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(COURT));
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-admin"));
        when(courtRepository.save(COURT)).thenReturn(COURT);
        final CourtGeneral courtResults = adminService.saveGeneral(SOME_SLUG, courtGeneral);
        assertThat(courtResults.getAlert()).isEqualTo(courtGeneral.getAlert());
        assertThat(courtResults.getAlertCy()).isEqualTo(courtGeneral.getAlertCy());
        assertThat(courtResults.getInfo()).isNotEqualTo(courtGeneral.getInfo());
        assertThat(courtResults.getInfoCy()).isNotEqualTo(courtGeneral.getInfoCy());
    }

    @Test
    void shouldSaveCourtAsSuperAdmin() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(COURT));
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-super-admin"));
        when(courtRepository.save(COURT)).thenReturn(COURT);
        final CourtGeneral courtResults = adminService.saveGeneral(SOME_SLUG, courtGeneral);
        assertThat(courtResults.getAlert()).isEqualTo(courtGeneral.getAlert());
        assertThat(courtResults.getAlertCy()).isEqualTo(courtGeneral.getAlertCy());
        assertThat(courtResults.getInfo()).isEqualTo(courtGeneral.getInfo());
        assertThat(courtResults.getInfoCy()).isEqualTo(courtGeneral.getInfoCy());
    }

    @Test
    void shouldUpdateAllCourtInfo() {
        CourtInfoUpdate info = new CourtInfoUpdate(singletonList(COURT.getSlug()), "updated info", "Welsh info");
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-super-admin"));

        adminService.updateMultipleCourtsInfo(info);

        verify(courtRepository, times(1)).updateInfoForSlugs(info.getCourts(), info.getInfo(), info.getInfoCy());
    }

}
