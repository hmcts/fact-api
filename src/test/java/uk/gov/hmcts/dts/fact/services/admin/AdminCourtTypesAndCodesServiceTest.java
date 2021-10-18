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
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtDxCode;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtTypesAndCodes;
import uk.gov.hmcts.dts.fact.model.admin.DxCode;
import uk.gov.hmcts.dts.fact.repositories.CourtDxCodesRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtTypeRepository;
import uk.gov.hmcts.dts.fact.util.MapCourtCode;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtTypesAndCodesService.class)
public class AdminCourtTypesAndCodesServiceTest {

    private static final int COURT_TYPE_COUNT = 3;
    private static final List<CourtType> COURT_TYPES = new ArrayList<>();
    private static final String COURT_SLUG = "some slug";
    private static final String NOT_FOUND = "Not found: ";
    private static final String GBS_CODE = "123";

    private static final List<CourtType> EXPECTED_COURT_TYPES_ENTITY = Arrays.asList(
        new CourtType(1,"test1"),
        new CourtType(2,"test2"),
        new CourtType(3,"test3")
    );

    private static final List<uk.gov.hmcts.dts.fact.model.admin.CourtType> EXPECTED_COURT_TYPES = Arrays.asList(
        new uk.gov.hmcts.dts.fact.model.admin.CourtType(1,"test1",null),
        new uk.gov.hmcts.dts.fact.model.admin.CourtType(2,"test2",null),
        new uk.gov.hmcts.dts.fact.model.admin.CourtType(3, "test3",1)
    );



    private static final List<DxCode> EXPECTED_COURT_DX_CODES = Arrays.asList(
        new DxCode("Code 1","explanation1","explanationCy1"),
        new DxCode("Code 2","explanation2","explanationCy2")
    );

    private static final List<CourtDxCode> EXPECTED_COURT_DX_CODE_ENTITY = Arrays.asList(
        new CourtDxCode(mock(Court.class),new uk.gov.hmcts.dts.fact.entity.DxCode("Code 1","explanation1","explanationCy1")),
        new CourtDxCode(mock(Court.class),new uk.gov.hmcts.dts.fact.entity.DxCode("Code 2","explanation2","explanationCy2"))

    );
    private static final CourtTypesAndCodes EXPECTED_COURT_TYPES_AND_CODES = new CourtTypesAndCodes(EXPECTED_COURT_TYPES,GBS_CODE,EXPECTED_COURT_DX_CODES);



    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtTypeRepository courtTypeRepository;

    @MockBean
    private MapCourtCode mapCourtCode;

    @MockBean
    private CourtDxCodesRepository courtDxCodesRepository;

    @MockBean
    private RolesProvider rolesProvider;

    @Autowired
    private AdminCourtTypesAndCodesService adminCourtTypesAndCodesService;

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

        assertThat(adminCourtTypesAndCodesService.getAllCourtTypes())
            .hasSize(COURT_TYPE_COUNT)
            .first()
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtType.class);
    }

    @Test
    void shouldReturnACourtsTypesAndCodes() {
        when(court.getCourtTypes()).thenReturn(COURT_TYPES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(mapCourtCode.mapCourtCodesForCourtTypeModel(anyList(), any())).thenReturn(EXPECTED_COURT_TYPES);

        assertThat(adminCourtTypesAndCodesService.getCourtTypesAndCodes(COURT_SLUG))
            .isInstanceOf(CourtTypesAndCodes.class);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingCourtTypesAndCodesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtTypesAndCodesService.getCourtTypesAndCodes(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }


    @Test
    void ShouldUpdateCourtTypesAndCodes() {
        when(court.getCourtTypes()).thenReturn(COURT_TYPES);
        when(court.getGbs()).thenReturn(GBS_CODE);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(mapCourtCode.mapCourtCodesForCourtTypeModel(anyList(), any())).thenReturn(EXPECTED_COURT_TYPES);
        when(mapCourtCode.mapCourtCodesForCourtEntity(anyList(), any())).thenReturn(court);
        when(rolesProvider.getRoles()).thenReturn(Collections.singletonList(FACT_SUPER_ADMIN));
        when(courtRepository.save(court)).thenReturn(court);
        when(adminCourtTypesAndCodesService.getCourtDxCodes(court)).thenReturn(EXPECTED_COURT_DX_CODES);
        when(adminCourtTypesAndCodesService.saveCourtTypesAndGbsCodes(court,EXPECTED_COURT_TYPES_AND_CODES,EXPECTED_COURT_TYPES_ENTITY)).thenReturn(court);
        when(courtDxCodesRepository.findByCourtId(any())).thenReturn(EXPECTED_COURT_DX_CODE_ENTITY);
        when(courtDxCodesRepository.saveAll(EXPECTED_COURT_DX_CODE_ENTITY)).thenReturn(EXPECTED_COURT_DX_CODE_ENTITY);

        assertThat(adminCourtTypesAndCodesService.updateCourtTypesAndCodes(COURT_SLUG, EXPECTED_COURT_TYPES_AND_CODES))
            .isEqualTo(EXPECTED_COURT_TYPES_AND_CODES);

    }

    @Test
    void shouldReturnNotFoundWhenUpdatingCourtTypesAndCodesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtTypesAndCodesService.updateCourtTypesAndCodes(COURT_SLUG, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }


}
