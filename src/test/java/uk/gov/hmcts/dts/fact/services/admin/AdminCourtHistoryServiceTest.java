package uk.gov.hmcts.dts.fact.services.admin;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.CourtHistory;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.Court;
import uk.gov.hmcts.dts.fact.repositories.CourtHistoryRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtHistoryService.class)
class AdminCourtHistoryServiceTest {

    @Autowired
    private AdminCourtHistoryService adminCourtHistoryService;

    @MockBean
    private CourtHistoryRepository courtHistoryRepository;

    @MockBean
    private AdminAuditService adminAuditService;

    @Captor
    ArgumentCaptor<CourtHistory> courtHistoryCaptor;

    private static List<CourtHistory> mockCourtHistories;
    private static final String EXP_EXCEPTION_MSG_PREFIX = "Not found: Court History with ID: ";
    private static final int COURT_HISTORY_ID = 132732;
    private static final int COURT_ID = 2122;

    private static CourtHistory courtHistory1;
    private static CourtHistory courtHistory2;
    private static CourtHistory courtHistory3;
    private static CourtHistory courtHistory4;


    @BeforeAll
    static void setUp() {
        mockCourtHistories = new ArrayList<>();
        courtHistory1 = new CourtHistory(
            1, 11, "fakeCourt1", LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2007-12-03T10:15:30"));
        courtHistory2 = new CourtHistory(
            2, 11332423, "fakeCourt2", null,
            null);
        courtHistory3 = new CourtHistory(
            3, 11, "fakeCourt3", LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2023-12-03T11:15:30"));
        courtHistory4 = new CourtHistory(
            4, 11, "fakeCourt3", LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2024-03-03T10:15:30"));

        mockCourtHistories.add(courtHistory1);
        mockCourtHistories.add(courtHistory2);
        mockCourtHistories.add(courtHistory3);
        mockCourtHistories.add(courtHistory4);
    }

    @Test
    void shouldReturnAllCourtNamesHistories() {
        when(courtHistoryRepository.findAll()).thenReturn(mockCourtHistories);

        assertThat(adminCourtHistoryService.getAllCourtHistory()).hasSize(mockCourtHistories.size());
    }

    @Test
    void shouldThrowExceptionWhenCourtNotFound() throws NotFoundException {
        when(courtHistoryRepository.findById(COURT_HISTORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtHistoryService.getCourtHistoryById(COURT_HISTORY_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(EXP_EXCEPTION_MSG_PREFIX + COURT_HISTORY_ID);
    }

    @Test
    void shouldGetAllTheCourtNameHistoriesOfGivenCourt() {
        when(courtHistoryRepository.findAllBySearchCourtId(COURT_ID)).thenReturn(mockCourtHistories);

        assertThat(adminCourtHistoryService.getCourtHistoryByCourtId(COURT_ID))
            .hasSize(mockCourtHistories.size());
    }

    @Test
    void shouldGetAllTheCourtNameHistoriesByCourtName() {
        List<CourtHistory> fakeCourtHistory = new ArrayList<>();
        fakeCourtHistory.add(courtHistory1);
        when(courtHistoryRepository.findAllByCourtName("fakeCourt1")).thenReturn(fakeCourtHistory);

        assertThat(adminCourtHistoryService.getCourtHistoryByCourtName("fakeCourt1"))
            .hasSize(fakeCourtHistory.size());
    }

    @Test
    void shouldBeAbleToAddACourtNameHistory() {
        uk.gov.hmcts.dts.fact.model.admin.CourtHistory courtHistoryToSave =
            new uk.gov.hmcts.dts.fact.model.admin.CourtHistory(courtHistory1);
        when(courtHistoryRepository.save(any(CourtHistory.class))).thenReturn(courtHistory1);
        assertThat(adminCourtHistoryService.addCourtHistory(courtHistoryToSave))
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtHistory.class);
    }

    @Test
    void shouldNotBeAbleToUpdateHistoryThatDoesNotExist() {
        uk.gov.hmcts.dts.fact.model.admin.CourtHistory iDontExist =
            new uk.gov.hmcts.dts.fact.model.admin.CourtHistory(courtHistory1);
        when(courtHistoryRepository.findById(iDontExist.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtHistoryService.updateCourtHistory(iDontExist))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(EXP_EXCEPTION_MSG_PREFIX + iDontExist.getId());
        verify(courtHistoryRepository, never()).save(any());

    }

    @Test
    void shouldBeAbleToUpdateExistingCourtHistory() {
        CourtHistory updatedCourtHistory3 = new CourtHistory(
            3, 11, "IWantToBeCalledThisNow", LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2023-12-03T11:15:30"));
        uk.gov.hmcts.dts.fact.model.admin.CourtHistory updatedCourtHistory3Model =
            new uk.gov.hmcts.dts.fact.model.admin.CourtHistory(updatedCourtHistory3);
        when(courtHistoryRepository.findById(updatedCourtHistory3Model.getId())).thenReturn(Optional.ofNullable(courtHistory3));
        when(courtHistoryRepository.save(any(CourtHistory.class))).thenReturn(courtHistory3);

        assertThat(adminCourtHistoryService.updateCourtHistory(updatedCourtHistory3Model))
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtHistory.class);

        verify(courtHistoryRepository).save(courtHistoryCaptor.capture());

        assertThat(courtHistoryCaptor.getValue().getCourtName()).isEqualTo("IWantToBeCalledThisNow");
        assertThat(courtHistoryCaptor.getValue().getSearchCourtId()).isEqualTo(11);
        assertThat(courtHistoryCaptor.getValue().getId()).isEqualTo(3);
        assertThat(courtHistoryCaptor.getValue().getCreatedAt()).isEqualTo(LocalDateTime.parse("2023-12-03T11:15:30"));
    }
}
