package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration(classes = AdminCourtGeneralInfoService.class)
public class AdminCourtGeneralInfoServiceTest {
    private static final String COURT_SLUG = "some slug";
    private static final String COURT_ALERT = "English alert";
    private static final String COURT_ALERT_CY = "Welsh alert";
    private static final CourtGeneralInfo COURT_GENERAL_INFO = new CourtGeneralInfo(false, false, false, null, null, COURT_ALERT, COURT_ALERT_CY);

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

    @Test
    void shouldReturnExpectedInPersonAndAccessSchemeWithoutInPersonInfo() {
        when(court.getInPerson()).thenReturn(null);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        final CourtGeneralInfo result = adminService.getCourtGeneralInfoBySlug(COURT_SLUG);
        assertThat(result.getInPerson()).isEqualTo(false);
        assertThat(result.getAccessScheme()).isEqualTo(false);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForInPersonTest() {
        return Stream.of(
            Arguments.of(false, false, false),
            Arguments.of(false, true, false),
            Arguments.of(true, false, false),
            Arguments.of(true, true, true)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForInPersonTest")
    void shouldReturnExpectedInPersonAndAccessSchemeWithInPersonInfo(final Boolean isInPersonCourt, final Boolean isAccessScheme, final Boolean expectedAccessScheme) {
        final InPerson inPerson = mock(InPerson.class);
        when(court.getInPerson()).thenReturn(inPerson);
        when(inPerson.getIsInPerson()).thenReturn(isInPersonCourt);
        when(inPerson.getAccessScheme()).thenReturn(isAccessScheme);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        final CourtGeneralInfo result = adminService.getCourtGeneralInfoBySlug(COURT_SLUG);
        assertThat(result.getInPerson()).isEqualTo(isInPersonCourt);
        assertThat(result.getAccessScheme()).isEqualTo(expectedAccessScheme);
    }
}
