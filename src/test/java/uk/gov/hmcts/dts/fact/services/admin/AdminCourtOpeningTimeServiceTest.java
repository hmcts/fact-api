package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.OpeningTime;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.OpeningTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtOpeningTimeService.class)
public class AdminCourtOpeningTimeServiceTest {
    private static final String COURT_SLUG = "some slug";
    private static final int TEST_TYPE_ID1 = 1;
    private static final int TEST_TYPE_ID2 = 2;
    private static final int TEST_TYPE_ID3 = 3;
    private static final String TEST_HOURS = "testHours";
    private static final String ENGLISH_TYPE1 = "Telephone enquiries answered";
    private static final String WELSH_TYPE1 = ENGLISH_TYPE1 + " cy";
    private static final String ENGLISH_TYPE2 = "Court open";
    private static final String WELSH_TYPE2 = ENGLISH_TYPE2 + " cy";
    private static final String ENGLISH_TYPE3 = "Counter open";
    private static final String WELSH_TYPE3 = ENGLISH_TYPE3 + " cy";
    private static final String NOT_FOUND = "Not found: ";

    private static final int OPENING_TIME_COUNT = 3;
    private static final List<CourtOpeningTime> COURT_OPENING_TIMES = new ArrayList<>();

    private static final List<OpeningTime> EXPECTED_OPENING_TIMES = Arrays.asList(
        new OpeningTime(TEST_TYPE_ID1, TEST_HOURS),
        new OpeningTime(TEST_TYPE_ID2, TEST_HOURS),
        new OpeningTime(TEST_TYPE_ID3, TEST_HOURS)
    );

    private static final uk.gov.hmcts.dts.fact.entity.OpeningType OPENING_TYPE1 = new uk.gov.hmcts.dts.fact.entity.OpeningType(TEST_TYPE_ID1, ENGLISH_TYPE1, WELSH_TYPE1);
    private static final uk.gov.hmcts.dts.fact.entity.OpeningType OPENING_TYPE2 = new uk.gov.hmcts.dts.fact.entity.OpeningType(TEST_TYPE_ID2, ENGLISH_TYPE2, WELSH_TYPE2);
    private static final uk.gov.hmcts.dts.fact.entity.OpeningType OPENING_TYPE3 = new uk.gov.hmcts.dts.fact.entity.OpeningType(TEST_TYPE_ID3, ENGLISH_TYPE3, WELSH_TYPE3);
    private static final List<uk.gov.hmcts.dts.fact.entity.OpeningType> OPENING_TYPES = Arrays.asList(OPENING_TYPE1, OPENING_TYPE2, OPENING_TYPE3);

    @Autowired
    private AdminCourtOpeningTimeService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private OpeningTypeRepository openingTypeRepository;

    @MockBean
    private AdminAuditService adminAuditService;

    @Mock
    private Court court;

    @BeforeAll
    static void setUp() {
        for (int i = 0; i < OPENING_TIME_COUNT; i++) {
            final CourtOpeningTime courtOpeningTime = mock(CourtOpeningTime.class);
            when(courtOpeningTime.getOpeningTime()).thenReturn(mock(uk.gov.hmcts.dts.fact.entity.OpeningTime.class));
            COURT_OPENING_TIMES.add(courtOpeningTime);
        }
    }

    @Test
    void shouldReturnAllOpeningTimes() {
        when(court.getCourtOpeningTimes()).thenReturn(COURT_OPENING_TIMES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        assertThat(adminService.getCourtOpeningTimesBySlug(COURT_SLUG))
            .hasSize(OPENING_TIME_COUNT)
            .first()
            .isInstanceOf(OpeningTime.class);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingOpeningTimesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getCourtOpeningTimesBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldUpdateCourtOpeningTimes() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(openingTypeRepository.findAll()).thenReturn(OPENING_TYPES);
        when(court.getCourtOpeningTimes()).thenReturn(COURT_OPENING_TIMES);
        when(courtRepository.save(court)).thenReturn(court);

        List<OpeningTime> results = adminService.updateCourtOpeningTimes(COURT_SLUG, EXPECTED_OPENING_TIMES);
        assertThat(adminService.updateCourtOpeningTimes(COURT_SLUG, EXPECTED_OPENING_TIMES))
            .hasSize(OPENING_TIME_COUNT)
            .containsExactlyElementsOf(EXPECTED_OPENING_TIMES);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court opening times",
                                                           EXPECTED_OPENING_TIMES,
                                                           results, COURT_SLUG);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingOpeningTimesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateCourtOpeningTimes(COURT_SLUG, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }


}
