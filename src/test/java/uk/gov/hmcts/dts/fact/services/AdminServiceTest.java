package uk.gov.hmcts.dts.fact.services;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.dts.fact.controllers.admin.AdminCourtsController.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.controllers.admin.AdminCourtsController.FACT_SUPER_ADMIN;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AdminService.class)
class AdminServiceTest {

    private static final Court COURT = new Court();
    private static final AreaOfLaw AOL = new AreaOfLaw();
    private static final String SOME_SLUG = "some-slug";
    public static final String SLUG = "slug";
    public static final String LAT = "lat";
    public static final String INFO = "info";
    private static CourtGeneral courtGeneral = new CourtGeneral();
    private static ObjectNode courtPatch = JsonNodeFactory.instance.objectNode();

    @Autowired
    private AdminService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private RolesProvider rolesProvider;

    @BeforeEach
    void setUp() {
        AOL.setName("area of law");
        COURT.setSlug(SLUG);
        COURT.setName("some-name");
        COURT.setNameCy("some-name-cy");
        COURT.setInfo("some-info");
        COURT.setInfoCy("some-info-cy");
        COURT.setAlert("some-urgent-message");
        COURT.setAlertCy("some-urgent-message-cy");
        COURT.setDisplayed(true);
        COURT.setAreasOfLaw(singletonList(AOL));
        COURT.setContacts(new ArrayList<>());
        COURT.setCourtTypes(new ArrayList<>());
        COURT.setEmails(new ArrayList<>());
        COURT.setAddresses(new ArrayList<>());
        COURT.setFacilities(new ArrayList<>());
        COURT.setOpeningTimes(new ArrayList<>());
        COURT.setServiceAreaCourts(new ArrayList<>());
        COURT.setServiceAreas(new ArrayList<>());

        courtGeneral = new CourtGeneral(
            SLUG,
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre",
            "Birmingham Civil and Family Justice Centre Info",
            "Birmingham Civil and Family Justice Centre Info",
            true,
            "Birmingham Civil and Family Justice Centre Alert",
            "Birmingham Civil and Family Justice Centre Alert"
        );

        courtPatch.put(SLUG, "new-slug");
        courtPatch.put(INFO, "Helpful info");
        courtPatch.put("lat", 1.0);
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
        when(rolesProvider.getRoles()).thenReturn(singletonList(FACT_ADMIN));
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
        when(rolesProvider.getRoles()).thenReturn(singletonList(FACT_SUPER_ADMIN));
        when(courtRepository.save(COURT)).thenReturn(COURT);
        final CourtGeneral courtResults = adminService.saveGeneral(SOME_SLUG, courtGeneral);
        assertThat(courtResults.getAlert()).isEqualTo(courtGeneral.getAlert());
        assertThat(courtResults.getAlertCy()).isEqualTo(courtGeneral.getAlertCy());
        assertThat(courtResults.getInfo()).isEqualTo(courtGeneral.getInfo());
        assertThat(courtResults.getInfoCy()).isEqualTo(courtGeneral.getInfoCy());
    }

    @Test
    void shouldPatchCourtAsAdmin() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(COURT));
        when(rolesProvider.getRoles()).thenReturn(singletonList(FACT_ADMIN));
        when(courtRepository.save(COURT)).thenReturn(COURT);

        final uk.gov.hmcts.dts.fact.model.Court updatedCourt = adminService.save(SOME_SLUG, courtPatch);
        assertThat(updatedCourt.getAlert()).isEqualTo(COURT.getAlert());
        assertThat(updatedCourt.getSlug()).isEqualTo(courtPatch.get(SLUG).asText());
        assertThat(updatedCourt.getLat()).isEqualTo(courtPatch.get(LAT).asDouble());
        assertThat(updatedCourt.getInfo()).isNotEqualTo(courtPatch.get(INFO).asText());
    }

    @Test
    void shouldPatchCourtAsSuperAdmin() {
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(COURT));
        when(rolesProvider.getRoles()).thenReturn(singletonList(FACT_SUPER_ADMIN));

        when(courtRepository.save(COURT)).thenReturn(COURT);

        final uk.gov.hmcts.dts.fact.model.Court updatedCourt = adminService.save(SOME_SLUG, courtPatch);
        assertThat(updatedCourt.getAlert()).isEqualTo(COURT.getAlert());
        assertThat(updatedCourt.getSlug()).isEqualTo(courtPatch.get(SLUG).asText());
        assertThat(updatedCourt.getLat()).isEqualTo(courtPatch.get("lat").asDouble());
        assertThat(updatedCourt.getInfo()).isEqualTo(courtPatch.get(INFO).asText());
    }
}
