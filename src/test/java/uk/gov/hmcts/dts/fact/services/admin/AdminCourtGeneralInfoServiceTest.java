package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtGeneralInfoService.class)
public class AdminCourtGeneralInfoServiceTest {
    private static final String COURT_SLUG = "some slug";
    private static final String COURT_ALERT = "English alert";
    private static final String COURT_ALERT_CY = "Welsh alert";
    private static final CourtGeneralInfo COURT_GENERAL_INFO = new CourtGeneralInfo(false, null, null, null, COURT_ALERT, COURT_ALERT_CY);

    @Autowired
    private AdminCourtGeneralInfoService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private RolesProvider rolesProvider;

    @Mock
    private Court court;

    @Test
    void shouldReturnCourtGeneralInfo() {
        when(court.getAlert()).thenReturn(COURT_ALERT);
        when(court.getAlertCy()).thenReturn(COURT_ALERT_CY);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        assertThat(adminService.getCourtGeneralInfoBySlug(COURT_SLUG)).isEqualTo(COURT_GENERAL_INFO);
    }

    @Test
    void shouldUpdateCourtGeneralInfo() {
        when(court.getAlert()).thenReturn(COURT_ALERT);
        when(court.getAlertCy()).thenReturn(COURT_ALERT_CY);
        when(rolesProvider.getRoles()).thenReturn(Collections.singletonList(FACT_ADMIN));
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtRepository.save(court)).thenReturn(court);

        assertThat(adminService.updateCourtGeneralInfo(COURT_SLUG, COURT_GENERAL_INFO)).isEqualTo(COURT_GENERAL_INFO);
    }
}
