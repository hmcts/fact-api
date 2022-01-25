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
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration(classes = AdminCourtGeneralInfoService.class)
public class AdminCourtGeneralInfoServiceTest {
    private static final String COURT_NAME = "Test court name";
    private static final String COURT_DUPLICATED_NAME = "test-court-name-duplicate";
    private static final String COURT_SLUG = "some slug";
    private static final String COURT_ALERT = "English alert";
    private static final String COURT_ALERT_CY = "Welsh alert";
    private static final String COURT_INFO = "English info";
    private static final String COURT_INFO_CY = "Welsh info";
    private static final String NOT_FOUND = "Not found: ";

    private static final CourtGeneralInfo ADMIN_INPUT_COURT_GENERAL_INFO = new CourtGeneralInfo(COURT_NAME,true, false, true, COURT_INFO, COURT_INFO_CY, COURT_ALERT, COURT_ALERT_CY, false);
    private static final CourtGeneralInfo ADMIN_INPUT_COURT_GENERAL_INFO_DUPLICATE_NAME = new CourtGeneralInfo(COURT_DUPLICATED_NAME,true, false, true, COURT_INFO, COURT_INFO_CY, COURT_ALERT, COURT_ALERT_CY, false);
    private static final CourtGeneralInfo OUTPUT_COURT_GENERAL_INFO = new CourtGeneralInfo(COURT_NAME, true, true, true, COURT_INFO, COURT_INFO_CY, COURT_ALERT, COURT_ALERT_CY, false);

    @Autowired
    private AdminCourtGeneralInfoService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private RolesProvider rolesProvider;

    @MockBean
    private AdminAuditService adminAuditService;

    @Mock
    private Court court;

    @Mock
    private InPerson inPerson;

    @Test
    void shouldReturnCourtGeneralInfo() {
        setUpCourtGeneralInfo();
        assertThat(adminService.getCourtGeneralInfoBySlug(COURT_SLUG)).isEqualTo(OUTPUT_COURT_GENERAL_INFO);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingGeneralInfoForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getCourtGeneralInfoBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void adminCanUpdateSelectedCourtGeneralInfo() {
        setUpCourtGeneralInfo();
        when(rolesProvider.getRoles()).thenReturn(Collections.singletonList(FACT_ADMIN));
        when(courtRepository.save(court)).thenReturn(court);

        CourtGeneralInfo results = adminService.updateCourtGeneralInfo(COURT_SLUG, ADMIN_INPUT_COURT_GENERAL_INFO);
        assertThat(results).isEqualTo(
            OUTPUT_COURT_GENERAL_INFO);

        verify(court, never()).setName(anyString());
        verify(court, never()).setInfo(anyString());
        verify(court, never()).setInfoCy(anyString());
        verify(court, never()).setDisplayed(anyBoolean());
        verify(inPerson, never()).setAccessScheme(anyBoolean());
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court general info",
                                                           new CourtGeneralInfo(court),
                                                           results, COURT_SLUG);
    }

    @Test
    void superAdminCanUpdateAllCourtGeneralInfo() {
        setUpCourtGeneralInfo();
        when(rolesProvider.getRoles()).thenReturn(Collections.singletonList(FACT_SUPER_ADMIN));
        when(courtRepository.save(court)).thenReturn(court);

        CourtGeneralInfo results = adminService.updateCourtGeneralInfo(COURT_SLUG, ADMIN_INPUT_COURT_GENERAL_INFO);
        assertThat(results).isEqualTo(
            OUTPUT_COURT_GENERAL_INFO);

        verify(court).setName(COURT_NAME);
        verify(court).setInfo(COURT_INFO);
        verify(court).setInfoCy(COURT_INFO_CY);
        verify(court).setDisplayed(true);
        verify(inPerson).setAccessScheme(true);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court general info",
                                                           new CourtGeneralInfo(court),
                                                           results, COURT_SLUG);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingGeneralInfoForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateCourtGeneralInfo(COURT_SLUG, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturnExpectedInPersonAndAccessSchemeWithoutInPersonInfo() {
        when(court.getInPerson()).thenReturn(null);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        final CourtGeneralInfo result = adminService.getCourtGeneralInfoBySlug(COURT_SLUG);
        assertThat(result.getInPerson()).isEqualTo(null);
        assertThat(result.getAccessScheme()).isEqualTo(false);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForInPersonTest() {
        return Stream.of(
            Arguments.of(false, false, false),
            Arguments.of(false, true, false),
            Arguments.of(true, false, false),
            Arguments.of(true, true, true),
            Arguments.of(null, false, false),
            Arguments.of(null, true, false)
        );
    }

    @Test
    void shouldReturnErrorIfCourtNameIsADuplicated() {
        when(rolesProvider.getRoles()).thenReturn(Collections.singletonList(FACT_SUPER_ADMIN));
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtRepository.findBySlug(COURT_DUPLICATED_NAME)).thenReturn(Optional.of(court));
        when(court.getName()).thenReturn(COURT_NAME);

        assertThatThrownBy(() -> adminService.updateCourtGeneralInfo(COURT_SLUG, ADMIN_INPUT_COURT_GENERAL_INFO_DUPLICATE_NAME))
            .isInstanceOf(DuplicatedListItemException.class)
            .hasMessage("Court already exists with slug: " + COURT_DUPLICATED_NAME);

        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
        verify(courtRepository).findBySlug(COURT_SLUG);
        verify(courtRepository).findBySlug(COURT_DUPLICATED_NAME);
    }

    @Test
    void checkIfUpdatedCourtIsValidShouldReturnErrorIfNameIsDuplicated() {
        when(courtRepository.findBySlug(COURT_DUPLICATED_NAME))
            .thenReturn(Optional.of(new Court()));
        assertThatThrownBy(() -> adminService.checkIfUpdatedCourtIsValid(COURT_SLUG, COURT_DUPLICATED_NAME))
            .isInstanceOf(DuplicatedListItemException.class)
            .hasMessage("Court already exists with slug: " + COURT_DUPLICATED_NAME);
        verify(courtRepository, atMostOnce()).findBySlug(COURT_SLUG);
    }

    @Test
    void checkIfUpdatedCourtIsValidShouldNotReturnErrorIfNameIsNotDuplicated() {
        when(courtRepository.findBySlug(COURT_NAME))
            .thenReturn(Optional.empty());
        assertDoesNotThrow(() -> adminService.checkIfUpdatedCourtIsValid(COURT_SLUG, COURT_NAME));
        verify(courtRepository, atMostOnce()).findBySlug(COURT_SLUG);
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

    private void setUpCourtGeneralInfo() {
        when(court.getName()).thenReturn(COURT_NAME);
        when(court.getAlert()).thenReturn(COURT_ALERT);
        when(court.getAlertCy()).thenReturn(COURT_ALERT_CY);
        when(court.getInfo()).thenReturn(COURT_INFO);
        when(court.getInfoCy()).thenReturn(COURT_INFO_CY);
        when(court.getDisplayed()).thenReturn(true);

        when(inPerson.getIsInPerson()).thenReturn(true);
        when(inPerson.getAccessScheme()).thenReturn(true);
        when(court.getInPerson()).thenReturn(inPerson);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
    }
}
