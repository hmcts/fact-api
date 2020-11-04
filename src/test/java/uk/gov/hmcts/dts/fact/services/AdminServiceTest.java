package uk.gov.hmcts.dts.fact.services;

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
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AdminService.class)
public class AdminServiceTest {

    @Autowired
    AdminService adminService;

    @MockBean
    CourtRepository courtRepository;

    @MockBean
    RolesProvider rolesProvider;

    private static final String SOME_SLUG = "some-slug";

    @Test
    void shouldReturnAllCourts() {
        final Court mock = mock(Court.class);
        when(courtRepository.findAll()).thenReturn(singletonList(mock));
        List<CourtReference> results = adminService.getAllCourts();
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
        final Court court = new Court();
        court.setName("some-name");
        court.setNameCy("some-name");
        court.setInfo("some-name");
        court.setInfoCy("some-name");
        court.setAlert("some-name");
        court.setAlertCy("some-name");
        court.setDisplayed(true);

        final CourtGeneral courtGeneral = new CourtGeneral(
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre Info",
            "Birmingham Civil and Family Justice Centre Info",
            true,
            "Birmingham Civil and Family Justice Centre Alert",
            "Birmingham Civil and Family Justice Centre Alert"
        );

        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(court));
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-admin"));
        when(courtRepository.save(court)).thenReturn(court);
        CourtGeneral courtResults = adminService.saveGeneral(SOME_SLUG, courtGeneral);
        assertThat(courtResults.getAlert()).isEqualTo(courtGeneral.getAlert());
        assertThat(courtResults.getAlertCy()).isEqualTo(courtGeneral.getAlertCy());
        assertThat(courtResults.getInfo()).isNotEqualTo(courtGeneral.getInfo());
        assertThat(courtResults.getInfoCy()).isNotEqualTo(courtGeneral.getInfoCy());
    }

    @Test
    void shouldSaveCourtAsSuperAdmin() {
        final Court court = new Court();
        court.setName("some-name");
        court.setNameCy("some-name");
        court.setInfo("some-name");
        court.setInfoCy("some-name");
        court.setAlert("some-name");
        court.setAlertCy("some-name");
        court.setDisplayed(true);

        final CourtGeneral courtGeneral = new CourtGeneral(
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre Info",
            "Birmingham Civil and Family Justice Centre Info",
            true,
            "Birmingham Civil and Family Justice Centre Alert",
            "Birmingham Civil and Family Justice Centre Alert"
        );

        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(court));
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-super-admin"));
        when(courtRepository.save(court)).thenReturn(court);
        CourtGeneral courtResults = adminService.saveGeneral(SOME_SLUG, courtGeneral);
        assertThat(courtResults.getAlert()).isEqualTo(courtGeneral.getAlert());
        assertThat(courtResults.getAlertCy()).isEqualTo(courtGeneral.getAlertCy());
        assertThat(courtResults.getInfo()).isEqualTo(courtGeneral.getInfo());
        assertThat(courtResults.getInfoCy()).isEqualTo(courtGeneral.getInfoCy());
    }
}
