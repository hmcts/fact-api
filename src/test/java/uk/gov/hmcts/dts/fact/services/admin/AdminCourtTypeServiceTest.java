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
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtTypeRepository;
import uk.gov.hmcts.dts.fact.util.MapCourtCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtTypesService.class)
public class AdminCourtTypeServiceTest {

    private static final int COURT_TYPE_COUNT = 3;
    private static final List<CourtType> COURT_TYPES = new ArrayList<>();
    private static final String COURT_SLUG = "some slug";
    private static final String NOT_FOUND = "Not found: ";
    private static final String TEST_MESSAGE = "Test Message";

    private static final List<CourtType> EXPECTED_COURT_TYPES_ENTITY = Arrays.asList(
        new CourtType(1,"test1"),
        new CourtType(2,"test2"),
        new CourtType(3,"test3")
    );

    private static final List<uk.gov.hmcts.dts.fact.model.admin.CourtType> EXPECTED_COURT_TYPES = Arrays.asList(
        new uk.gov.hmcts.dts.fact.model.admin.CourtType(1,"test1",null),
        new uk.gov.hmcts.dts.fact.model.admin.CourtType(2,"test2",null),
        new uk.gov.hmcts.dts.fact.model.admin.CourtType(3, "test3",null)
    );

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtTypeRepository courtTypeRepository;

    @MockBean
    private MapCourtCode mapCourtCode;

    @Autowired
    private AdminCourtTypesService adminCourtTypesService;

    @MockBean
    private AdminAuditService adminAuditService;

    @Mock
    private Court court;

    @BeforeAll
    static void setUp() {
        for (int i = 0; i < COURT_TYPE_COUNT; i++) {
            final CourtType courtType = mock(CourtType.class);
            COURT_TYPES.add(courtType);
        }
    }

    @Test
    void shouldReturnAllCourtTypes() {

        when(courtTypeRepository.findAll()).thenReturn(EXPECTED_COURT_TYPES_ENTITY);

        assertThat(adminCourtTypesService.getAllCourtTypes())
            .hasSize(COURT_TYPE_COUNT)
            .first()
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtType.class);
    }

    @Test
    void shouldReturnCourtCourtTypes() {
        when(court.getCourtTypes()).thenReturn(COURT_TYPES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(mapCourtCode.mapCourtCodesForCourtTypeModel(anyList(), any())).thenReturn(EXPECTED_COURT_TYPES);

        assertThat(adminCourtTypesService.getCourtCourtTypesBySlug(COURT_SLUG))
            .hasSize(COURT_TYPE_COUNT)
            .first()
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtType.class);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingCourtTypesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtTypesService.getCourtCourtTypesBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldUpdateCourtCourtTypes() {
        when(court.getCourtTypes()).thenReturn(COURT_TYPES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(court.getCourtTypes()).thenReturn(COURT_TYPES);
        when(mapCourtCode.mapCourtCodesForCourtEntity(anyList(), any())).thenReturn(court);
        when(courtRepository.save(court)).thenReturn(court);
        when(adminCourtTypesService.saveNewCourtCourtTypes(court, EXPECTED_COURT_TYPES)).thenReturn(
            EXPECTED_COURT_TYPES);

        List<uk.gov.hmcts.dts.fact.model.admin.CourtType> results =
            adminCourtTypesService.updateCourtCourtTypes(COURT_SLUG, EXPECTED_COURT_TYPES);
        assertThat(adminCourtTypesService.updateCourtCourtTypes(COURT_SLUG, EXPECTED_COURT_TYPES))
            .hasSize(COURT_TYPE_COUNT)
            .containsExactlyElementsOf(EXPECTED_COURT_TYPES);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court court types",
                                                           COURT_TYPES.stream()
                                                               .map(uk.gov.hmcts.dts.fact.model.admin.CourtType::new)
                                                               .collect(toList()),
                                                           results, COURT_SLUG);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingCourtTypesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtTypesService.updateCourtCourtTypes(COURT_SLUG, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturnIllegalArgumentForUnknownCourtType() {
        when(court.getCourtTypes()).thenReturn(COURT_TYPES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(mapCourtCode.mapCourtCodesForCourtEntity(anyList(), any())).thenThrow(new IllegalArgumentException(TEST_MESSAGE));

        assertThatThrownBy(() -> adminCourtTypesService.updateCourtCourtTypes(COURT_SLUG, EXPECTED_COURT_TYPES))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(TEST_MESSAGE);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }
}
