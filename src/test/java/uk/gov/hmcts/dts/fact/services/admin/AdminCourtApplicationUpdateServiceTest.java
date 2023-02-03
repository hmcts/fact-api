package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.repositories.CourtApplicationUpdateRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyIterable;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtApplicationUpdateService.class)
class AdminCourtApplicationUpdateServiceTest {
    private static final String TEST_SLUG = "court-slug";
    private static final Court MOCK_COURT = mock(Court.class);
    private static final String TEST_TYPE_1 = "English type 1";
    private static final String TEST_TYPE_2 = "English type 2";
    private static final String TEST_TYPE_3 = "English type 3";
    private static final String TEST_TYPE_CY_1 = "Welsh type 1";
    private static final String TEST_TYPE_CY_2 = "Welsh type 2";
    private static final String TEST_TYPE_CY_3 = "Welsh type 3";
    private static final String TEST_EMAIL_1 = "test@test-1.com";
    private static final String TEST_EMAIL_2 = "test@test-2.com";
    private static final String TEST_EMAIL_3 = "test@test-3.com";
    private static final String TEST_EXTERNAL_LINK_1 = "www.test1.com";
    private static final String TEST_EXTERNAL_LINK_2 = "www.test2.com";
    private static final String TEST_EXTERNAL_LINK_3 = "www.test3.com";
    private static final String TEST_EXTERNAL_LINK_DESC_1 = "English link description 1";
    private static final String TEST_EXTERNAL_LINK_DESC_2 = "English link description 2";
    private static final String TEST_EXTERNAL_LINK_DESC_3 = "English link description 3";
    private static final String TEST_EXTERNAL_LINK_DESC_CY_1 = "Welsh link description 1";
    private static final String TEST_EXTERNAL_LINK_DESC_CY_2 = "Welsh link description 2";
    private static final String TEST_EXTERNAL_LINK_DESC_CY_3 = "Welsh link description 3";
    private static final String NOT_FOUND = "Not found: ";


    private static final uk.gov.hmcts.dts.fact.entity.ApplicationUpdate APPLICATION_UPDATE_ENTITY_1 =
        new uk.gov.hmcts.dts.fact.entity.ApplicationUpdate(TEST_TYPE_1, TEST_TYPE_CY_1, TEST_EMAIL_1, TEST_EXTERNAL_LINK_1,
                                                           TEST_EXTERNAL_LINK_DESC_1, TEST_EXTERNAL_LINK_DESC_CY_1);
    private static final uk.gov.hmcts.dts.fact.entity.ApplicationUpdate APPLICATION_UPDATE_ENTITY_2 =
        new uk.gov.hmcts.dts.fact.entity.ApplicationUpdate(TEST_TYPE_2, TEST_TYPE_CY_2, TEST_EMAIL_2, TEST_EXTERNAL_LINK_2,
                                                           TEST_EXTERNAL_LINK_DESC_2, TEST_EXTERNAL_LINK_DESC_CY_2);
    private static final uk.gov.hmcts.dts.fact.entity.ApplicationUpdate APPLICATION_UPDATE_ENTITY_3 =
        new uk.gov.hmcts.dts.fact.entity.ApplicationUpdate(TEST_TYPE_3, TEST_TYPE_CY_3, TEST_EMAIL_3, TEST_EXTERNAL_LINK_3,
                                                           TEST_EXTERNAL_LINK_DESC_3, TEST_EXTERNAL_LINK_DESC_CY_3);

    private static final List<ApplicationUpdate> EXPECTED_APPLICATION_UPDATES = Arrays.asList(
        new ApplicationUpdate(TEST_TYPE_1, TEST_TYPE_CY_1, TEST_EMAIL_1, TEST_EXTERNAL_LINK_1,
                              TEST_EXTERNAL_LINK_DESC_1, TEST_EXTERNAL_LINK_DESC_CY_1),
        new ApplicationUpdate(TEST_TYPE_2, TEST_TYPE_CY_2, TEST_EMAIL_2, TEST_EXTERNAL_LINK_2,
                              TEST_EXTERNAL_LINK_DESC_2, TEST_EXTERNAL_LINK_DESC_CY_2),
        new ApplicationUpdate(TEST_TYPE_3, TEST_TYPE_CY_3, TEST_EMAIL_3, TEST_EXTERNAL_LINK_3,
                              TEST_EXTERNAL_LINK_DESC_3, TEST_EXTERNAL_LINK_DESC_CY_3)
    );

    private static final List<CourtApplicationUpdate> COURT_APPLICATION_UPDATES = Arrays.asList(
        new CourtApplicationUpdate(MOCK_COURT, APPLICATION_UPDATE_ENTITY_1, 0),
        new CourtApplicationUpdate(MOCK_COURT, APPLICATION_UPDATE_ENTITY_2, 1),
        new CourtApplicationUpdate(MOCK_COURT, APPLICATION_UPDATE_ENTITY_3, 2)
    );

    @Autowired
    private AdminCourtApplicationUpdateService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtApplicationUpdateRepository courtApplicationUpdateRepository;

    @MockBean
    private AdminAuditService adminAuditService;

    @Test
    void shouldReturnAllApplicationUpdates() {
        when(MOCK_COURT.getCourtApplicationUpdates()).thenReturn(COURT_APPLICATION_UPDATES);
        when(courtRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.of(MOCK_COURT));

        final List<ApplicationUpdate> results = adminService.getApplicationUpdatesBySlug(TEST_SLUG);
        assertThat(results).hasSize(EXPECTED_APPLICATION_UPDATES.size());
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingApplicationUpdatesForNonExistentCourt() {
        when(courtRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getApplicationUpdatesBySlug(TEST_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + TEST_SLUG);
    }

    @Test
    void shouldUpdateCourtApplicationUpdates() {
        when(courtRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.of(MOCK_COURT));
        when(MOCK_COURT.getCourtApplicationUpdates()).thenReturn(COURT_APPLICATION_UPDATES);
        when(courtApplicationUpdateRepository.saveAll(any())).thenReturn(COURT_APPLICATION_UPDATES);

        final List<ApplicationUpdate> results = adminService.updateApplicationUpdates(TEST_SLUG, EXPECTED_APPLICATION_UPDATES);
        verify(courtApplicationUpdateRepository).deleteAll(COURT_APPLICATION_UPDATES);
        verify(courtApplicationUpdateRepository).saveAll(anyIterable());
        assertThat(results)
            .hasSize(EXPECTED_APPLICATION_UPDATES.size())
            .containsAnyElementsOf(EXPECTED_APPLICATION_UPDATES);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court application updates list",
                                                           EXPECTED_APPLICATION_UPDATES,
                                                           results, TEST_SLUG);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingApplicationUpdatesForNonExistentCourt() {
        when(courtRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateApplicationUpdates(TEST_SLUG, EXPECTED_APPLICATION_UPDATES))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + TEST_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), any(), any(), anyString());
    }
}


