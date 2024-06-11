package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtHistory;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtHistoryRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtHistoryService.class)
@SuppressWarnings({"PMD.UseUnderscoresInNumericLiterals"})
class AdminCourtHistoryServiceTest {

    @Autowired
    private AdminCourtHistoryService adminCourtHistoryService;

    @MockBean
    private CourtHistoryRepository courtHistoryRepository;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private AdminAuditService adminAuditService;

    @Captor
    ArgumentCaptor<CourtHistory> courtHistoryCaptor;

    @Captor
    ArgumentCaptor<List<CourtHistory>> courtHistoriesCaptor;

    @Captor
    ArgumentCaptor<Integer> idCaptor;


    private static List<CourtHistory> mockCourtHistories;
    private static final Court FAKE_CURRENT_COURT = new Court();
    private static final String EXP_EXCEPTION_MSG_PREFIX = "Not found: Court History with ID: ";
    private static final String NON_FOUND_COURT_MSG_PREFIX = "Not found: Court cannot be found. Slug: ";
    private static final int DONT_EXIST_COURT_HISTORY_ID = 132732;
    private static final int COURT_ID = 2122;
    private static final String COURT_SLUG = "i-am-a-slug";
    private static final String DATE1 = "2007-12-03T10:15:30";
    private static final String DATE2 = "2024-02-03T10:15:30";
    private static final String FAKE_COURT_NAME1 = "fakeCourt1";

    private static CourtHistory courtHistory1;
    private static CourtHistory courtHistory2;


    @BeforeEach
    void setUp() {
        mockCourtHistories = new ArrayList<>();
        courtHistory1 = new CourtHistory(
            1, 11, FAKE_COURT_NAME1, LocalDateTime.parse(DATE2),
            LocalDateTime.parse("2007-12-03T10:15:30"), null);
        courtHistory2 = new CourtHistory(
            2, 11332423, "fakeCourt2", null, null, null);
        CourtHistory courtHistory3 = new CourtHistory(
                3, 11, "fakeCourt3", LocalDateTime.parse(DATE2),
                LocalDateTime.parse(DATE1), "");
        CourtHistory courtHistory4 = new CourtHistory(
                4, 11, "fakeCourt4", LocalDateTime.parse(DATE2),
                LocalDateTime.parse(DATE1), "Llys y Goron");

        mockCourtHistories.add(courtHistory1);
        mockCourtHistories.add(courtHistory3);
        mockCourtHistories.add(courtHistory4);
    }

    @Test
    void shouldReturnAllCourtNamesHistories() {
        when(courtHistoryRepository.findAll()).thenReturn(mockCourtHistories);

        assertThat(adminCourtHistoryService.getAllCourtHistory()).hasSize(3);
    }

    @Test
    void shouldNotRetrieveANonExistentCourt() {
        when(courtHistoryRepository.findById(DONT_EXIST_COURT_HISTORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtHistoryService.getCourtHistoryById(DONT_EXIST_COURT_HISTORY_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(EXP_EXCEPTION_MSG_PREFIX + DONT_EXIST_COURT_HISTORY_ID);
    }

    @Test
    void shouldGetAllTheCourtNameHistoriesOfGivenCourt() {
        when(courtHistoryRepository.findAllBySearchCourtId(COURT_ID)).thenReturn(mockCourtHistories);

        assertThat(adminCourtHistoryService.getCourtHistoryByCourtId(COURT_ID))
            .hasSize(mockCourtHistories.size());
    }

    @Test
    void shouldGetAllTheCourtNameHistoriesOfGivenCourtByItsSlug() {
        FAKE_CURRENT_COURT.setId(COURT_ID);
        FAKE_CURRENT_COURT.setSlug(COURT_SLUG);
        when(courtHistoryRepository.findAllBySearchCourtId(COURT_ID)).thenReturn(mockCourtHistories);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(FAKE_CURRENT_COURT));

        assertThat(adminCourtHistoryService.getCourtHistoryByCourtId(COURT_ID))
            .hasSize(mockCourtHistories.size());
    }

    @Test
    void shouldNotRetrieveCourtHistoriesOfNonExistentCourt() {
        assertThatThrownBy(() -> adminCourtHistoryService.getCourtHistoryByCourtSlug("i-dont-exist"))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NON_FOUND_COURT_MSG_PREFIX + "i-dont-exist");
    }

    @Test
    void shouldGetAllTheCourtNameHistoriesByCourtName() {
        List<CourtHistory> fakeCourtHistory = new ArrayList<>();
        fakeCourtHistory.add(courtHistory1);
        when(courtHistoryRepository.findAllByCourtName(FAKE_COURT_NAME1)).thenReturn(fakeCourtHistory);

        assertThat(adminCourtHistoryService.getCourtHistoryByCourtName(FAKE_COURT_NAME1))
            .hasSize(fakeCourtHistory.size());
    }

    @Test
    void shouldBeAbleToAddACourtNameHistory() {
        uk.gov.hmcts.dts.fact.model.admin.CourtHistory courtHistoryToSave =
            new uk.gov.hmcts.dts.fact.model.admin.CourtHistory(courtHistory2);
        when(courtHistoryRepository.save(any(CourtHistory.class))).thenReturn(courtHistory2);
        assertThat(adminCourtHistoryService.addCourtHistory(courtHistoryToSave))
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtHistory.class);
        verify(adminAuditService, times(1)).saveAudit("Create court history",
                                                      courtHistoryToSave, null, courtHistoryToSave.getCourtName());
    }

    @Test
    void shouldNotBeAbleToUpdateHistoryThatDoesNotExist() {
        uk.gov.hmcts.dts.fact.model.admin.CourtHistory doesNotExist =
            new uk.gov.hmcts.dts.fact.model.admin.CourtHistory(courtHistory1);
        when(courtHistoryRepository.findById(doesNotExist.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtHistoryService.updateCourtHistory(doesNotExist))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(EXP_EXCEPTION_MSG_PREFIX + doesNotExist.getId());
        verify(courtHistoryRepository, never()).save(any());
        verify(adminAuditService, never()).saveAudit(any(), any(), any(), any());
    }

    @Test
    void shouldBeAbleToUpdateExistingCourtHistory() {
        CourtHistory existingCourtHistory = new CourtHistory(
            3, 11, "IAmCalledThis", LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2023-12-03T11:15:30"), "Cymru");
        CourtHistory updatedCourtHistory = new CourtHistory(
            3, 11, "IWantToBeCalledThisNow", LocalDateTime.parse("2024-02-03T10:15:30"),
            LocalDateTime.parse("2023-12-03T11:15:30"), "Cymru");
        uk.gov.hmcts.dts.fact.model.admin.CourtHistory updatedCourtHistoryModel =
            new uk.gov.hmcts.dts.fact.model.admin.CourtHistory(updatedCourtHistory);

        when(courtHistoryRepository.findById(updatedCourtHistoryModel.getId())).thenReturn(Optional.of(existingCourtHistory));
        when(courtHistoryRepository.save(any(CourtHistory.class))).thenReturn(existingCourtHistory);

        assertThat(adminCourtHistoryService.updateCourtHistory(updatedCourtHistoryModel))
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtHistory.class);

        verify(adminAuditService).saveAudit(eq("Update court history"), isA(uk.gov.hmcts.dts.fact.model.admin.CourtHistory.class), isNull(),
                                            eq(updatedCourtHistoryModel.getCourtName()));
        verify(courtHistoryRepository).save(courtHistoryCaptor.capture());

        assertThat(courtHistoryCaptor.getValue().getCourtName()).isEqualTo("IWantToBeCalledThisNow");
        assertThat(courtHistoryCaptor.getValue().getSearchCourtId()).isEqualTo(11);
        assertThat(courtHistoryCaptor.getValue().getId()).isEqualTo(3);
        assertThat(courtHistoryCaptor.getValue().getCreatedAt()).isEqualTo(LocalDateTime.parse("2023-12-03T11:15:30"));
    }

    @Test
    void shouldBeAbleToDeleteCourtHistory() {
        when(courtHistoryRepository.findById(1)).thenReturn(Optional.ofNullable(courtHistory1));
        when(courtHistoryRepository.save(courtHistory1)).thenReturn(courtHistory1);
        assertThat(adminCourtHistoryService.deleteCourtHistoryById(1))
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtHistory.class);

        verify(courtHistoryRepository).deleteById(idCaptor.capture());
        verify(adminAuditService).saveAudit(eq("Delete court history"), any(), isNull(), eq(courtHistory1.getCourtName()));
        assertThat(idCaptor.getValue()).isEqualTo(1);
    }

    @Test
    void shouldNotBeAbleToDeleteCourtHistoryThatDoesNotExist() {
        when(courtHistoryRepository.findById(DONT_EXIST_COURT_HISTORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtHistoryService.deleteCourtHistoryById(DONT_EXIST_COURT_HISTORY_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(EXP_EXCEPTION_MSG_PREFIX + DONT_EXIST_COURT_HISTORY_ID);

        verify(courtHistoryRepository, never()).deleteById(any());
        verify(adminAuditService, never()).saveAudit(any(), any(), any(), any());
    }

    @Test
    void shouldBeAbleToDeleteAllTheCourtHistoryOfACourt() {
        when(courtHistoryRepository.deleteCourtHistoriesBySearchCourtId(11)).thenReturn(mockCourtHistories);

        assertThat(adminCourtHistoryService.deleteCourtHistoriesByCourtId(11))
            .isInstanceOf(List.class)
            .hasSize(3)
            .extracting("courtName")
            .contains("fakeCourt1","fakeCourt3", "fakeCourt4");

        verify(adminAuditService).saveAudit(eq("Delete court history"), any(), isNull(), isA(String.class));
    }

    @Test
    void shouldBeAbleToReplaceAllTheCourtHistoryOfACourt() {
        final String COURT_SLUG = "i-want-new-history";
        List<uk.gov.hmcts.dts.fact.model.admin.CourtHistory> modelUpdatedCourtHistories = List.of(new uk.gov.hmcts.dts.fact.model.admin.CourtHistory(
                80, null, "IAmCalledThis", LocalDateTime.parse("2024-02-03T10:15:30"),
                LocalDateTime.parse("2023-12-03T11:15:30"), "Cymru"));
        List<CourtHistory> updatedCourtHistories = List.of(new CourtHistory(
            80, null, "IWantToBeCalledThisNow", null,
            null, "Cymru"));

        FAKE_CURRENT_COURT.setSlug(COURT_SLUG);
        FAKE_CURRENT_COURT.setId(11);
        FAKE_CURRENT_COURT.setName("New Court");

        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(FAKE_CURRENT_COURT));
        when(courtHistoryRepository.saveAll(any())).thenReturn(updatedCourtHistories);

        assertThat(adminCourtHistoryService.updateCourtHistoriesBySlug(COURT_SLUG, modelUpdatedCourtHistories))
            .hasSize(1);

        verify(courtHistoryRepository, times(1)).saveAll(courtHistoriesCaptor.capture());
        verify(courtHistoryRepository, times(1)).findAllBySearchCourtId(11);
        verify(courtHistoryRepository, times(1)).deleteCourtHistoriesBySearchCourtId(11);
        verify(adminAuditService, times(1)).saveAudit(eq("Update court history"), any(), any(), eq("New Court"));

        assertThat(courtHistoriesCaptor.getValue())
            .isInstanceOf(List.class)
            .hasSize(1)
            .extracting("searchCourtId")
            .contains(11);
    }
}
