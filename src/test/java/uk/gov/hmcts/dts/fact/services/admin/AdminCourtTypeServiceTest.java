package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtTypesService.class)
public class AdminCourtTypeServiceTest {

    private static final int COURT_TYPE_COUNT = 3;
    private static final List<CourtType> COURT_TYPES = new ArrayList<>();
    private static final String COURT_SLUG = "some slug";




    private static final List<CourtType> EXPECTED_COURT_TYPES = Arrays.asList(
        new CourtType(1,"test1"),
        new CourtType(2,"test2"),
        new CourtType(3,"test3")
    );

    private static final List<uk.gov.hmcts.dts.fact.model.admin.CourtType> EXPECTED_COURT_TYPES1 = Arrays.asList(
        new uk.gov.hmcts.dts.fact.model.admin.CourtType(1,"test1",null),
        new uk.gov.hmcts.dts.fact.model.admin.CourtType(2,"test2",null),
        new uk.gov.hmcts.dts.fact.model.admin.CourtType(3, "test3",null)
    );

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtTypeRepository courtTypeRepository;

    @Autowired
    private AdminCourtTypesService adminCourtTypesService;


    @Mock
    private Court court;

    @Spy
    @Autowired
    final AdminCourtTypesService adminCourtTypesServiceSpy = new AdminCourtTypesService(courtRepository,courtTypeRepository);

    @BeforeAll
    static void setUp() {
        for (int i = 0; i < COURT_TYPE_COUNT; i++) {
            final CourtType courtType = mock(CourtType.class);
            COURT_TYPES.add(courtType);
        }

    }

    @Test
    void shouldReturnAllCourtTypes() {

        when(courtTypeRepository.findAll()).thenReturn(EXPECTED_COURT_TYPES);

        assertThat(adminCourtTypesService.getAllCourtTypes())
            .hasSize(COURT_TYPE_COUNT)
            .first()
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtType.class);
    }

    @Test
    void shouldReturnCourtCourtTypes() {
        when(court.getCourtTypes()).thenReturn(COURT_TYPES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(adminCourtTypesServiceSpy.mapCourtTypesCodes(anyList(), any())).thenReturn(EXPECTED_COURT_TYPES1);

        assertThat(adminCourtTypesServiceSpy.getCourtCourtTypesBySlug(COURT_SLUG))
            .hasSize(COURT_TYPE_COUNT)
            .first()
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtType.class);
    }



    @Test
    void shouldUpdateCourtCourtTypes() {
        when(court.getCourtTypes()).thenReturn(COURT_TYPES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtRepository.save(court)).thenReturn(court);
        when(adminCourtTypesServiceSpy.saveNewCourtCourtTypes(court, EXPECTED_COURT_TYPES1)).thenReturn(EXPECTED_COURT_TYPES1);

        assertThat(adminCourtTypesServiceSpy.updateCourtCourtTypes(COURT_SLUG, EXPECTED_COURT_TYPES1))
            .hasSize(COURT_TYPE_COUNT)
            .containsExactlyElementsOf(EXPECTED_COURT_TYPES1);
    }
}
