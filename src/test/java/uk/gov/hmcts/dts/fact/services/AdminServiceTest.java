package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.AdminCourt;
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
    void shouldReturnAdminCourtObject() {
        final Court mock = mock(Court.class);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(mock));
        assertThat(adminService.getCourtBySlug(SOME_SLUG)).isInstanceOf(AdminCourt.class);
    }

    @Test
    void shouldReturnCourtEntityObject() {
        final Court mock = mock(Court.class);
        when(courtRepository.findBySlug(SOME_SLUG)).thenReturn(Optional.of(mock));
        assertThat(adminService.getCourtEntityBySlug(SOME_SLUG)).isInstanceOf(Court.class);
    }

    @Test
    void shouldSaveCourt() {
        final Court mock = mock(Court.class);
        when(courtRepository.save(mock)).thenReturn(mock);
        assertThat(adminService.save(mock)).isInstanceOf(AdminCourt.class);
    }
}
