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
import uk.gov.hmcts.dts.fact.model.admin.OpeningTime;
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.OpeningTypeRepository;

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
    private static final int TEST_TYPE_ID1 = 1;
    private static final int TEST_TYPE_ID2 = 2;
    private static final int TEST_TYPE_ID3 = 3;
    private static final String TEST_HOURS = "testHours";
    private static final String ENGLISH_TYPE1 = "test1";
    private static final String WELSH_TYPE1 = "test2";
    private static final String ENGLISH_TYPE2 = "test3";
    private static final String WELSH_TYPE2 = "test4";
    private static final String ENGLISH_TYPE3 = "test5";
    private static final String WELSH_TYPE3 = "test6";

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
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(openingTypeRepository.findAll()).thenReturn(OPENING_TYPES);
        when(court.getCourtOpeningTimes()).thenReturn(COURT_OPENING_TIMES);
        when(courtRepository.save(court)).thenReturn(court);

        assertThat(adminService.updateCourtOpeningTimes(COURT_SLUG, EXPECTED_OPENING_TIMES))
            .hasSize(OPENING_TIME_COUNT)
            .containsExactlyElementsOf(EXPECTED_OPENING_TIMES);
    }

    @Test
    void shouldReturnAllOpeningTypes() {
        when(openingTypeRepository.findAll()).thenReturn(OPENING_TYPES);

        final List<OpeningType> results = adminService.getAllCourtOpeningTypes();
        assertThat(results).hasSize(OPENING_TYPES.size());
        assertThat(results.get(0).getType()).isEqualTo(ENGLISH_TYPE1);
        assertThat(results.get(0).getTypeCy()).isEqualTo(WELSH_TYPE1);
        assertThat(results.get(1).getType()).isEqualTo(ENGLISH_TYPE2);
        assertThat(results.get(1).getTypeCy()).isEqualTo(WELSH_TYPE2);
        assertThat(results.get(2).getType()).isEqualTo(ENGLISH_TYPE3);
        assertThat(results.get(2).getTypeCy()).isEqualTo(WELSH_TYPE3);
    }
}
