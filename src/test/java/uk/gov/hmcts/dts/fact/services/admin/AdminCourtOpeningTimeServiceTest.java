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
import uk.gov.hmcts.dts.fact.model.OpeningTime;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtOpeningTimeService.class)
public class AdminCourtOpeningTimeServiceTest {
    private static final String COURT_SLUG = "some slug";
    private static final String TEST_TYPE1 = "testType1";
    private static final String TEST_TYPE2 = "testType2";
    private static final String TEST_TYPE3 = "testType3";
    private static final String TEST_HOURS = "testHours";

    private static final int OPENING_TIME_COUNT = 3;
    private static final List<CourtOpeningTime> COURT_OPENING_TIMES = new ArrayList<>();

    private static final List<OpeningTime> EXPECTED_OPENING_TIMES = Arrays.asList(
        new OpeningTime(TEST_TYPE1, null, TEST_HOURS),
        new OpeningTime(TEST_TYPE2, null, TEST_HOURS),
        new OpeningTime(TEST_TYPE3, null, TEST_HOURS)
    );

    @Autowired
    private AdminCourtOpeningTimeService adminService;

    @MockBean
    private CourtRepository courtRepository;

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
    void shouldUpdateCourtOpeningTimes() {
        when(court.getCourtOpeningTimes()).thenReturn(COURT_OPENING_TIMES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtRepository.save(court)).thenReturn(court);

        assertThat(adminService.updateCourtOpeningTimes(COURT_SLUG, EXPECTED_OPENING_TIMES))
            .hasSize(OPENING_TIME_COUNT)
            .containsExactlyElementsOf(EXPECTED_OPENING_TIMES);
    }
}
