package uk.gov.hmcts.dts.fact.services.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLawSpoe;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawSpoeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.dts.fact.util.TestHelper.getResourceAsJson;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtSpoeAreasOfLawService.class)
public class AdminCourtSpoeAreasOfLawServiceTest {

    private static final String COURT_SLUG = "some slug";
    private static final int COURT_AREAS_OF_LAW_COUNT = 3;
    private static final String NOT_FOUND = "Not found: ";
    private static final String TEST_COURT_AREAS_OF_LAW_PATH = "court-spoe-areas-of-law.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final List<CourtAreaOfLawSpoe> COURT_SPOE_AREA_OF_LAWS = new ArrayList<>();

    @MockBean
    private CourtRepository courtRepository;


    @MockBean
    private AdminAuditService adminAuditService;

    @MockBean
    private CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository;

    @Mock
    private static Court court;

    @Autowired
    private AdminCourtSpoeAreasOfLawService adminCourtSpoeAreasOfLawService;

    @BeforeAll
    static void setUp() {
        uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLawOne = new uk.gov.hmcts.dts.fact.entity.AreaOfLaw();
        areaOfLawOne.setId(1);
        areaOfLawOne.setName("AreaOfLaw1");
        uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLawTwo = new uk.gov.hmcts.dts.fact.entity.AreaOfLaw();
        areaOfLawTwo.setId(2);
        areaOfLawTwo.setName("AreaOfLaw2");
        uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLawThree = new uk.gov.hmcts.dts.fact.entity.AreaOfLaw();
        areaOfLawThree.setId(3);
        areaOfLawThree.setName("AreaOfLaw3");

        COURT_SPOE_AREA_OF_LAWS.add(new CourtAreaOfLawSpoe(areaOfLawOne, court));
        COURT_SPOE_AREA_OF_LAWS.add(new CourtAreaOfLawSpoe(areaOfLawTwo, court));
        COURT_SPOE_AREA_OF_LAWS.add(new CourtAreaOfLawSpoe(areaOfLawThree, court));
    }


    @Test
    void shouldReturnAllSpoeAreasOfLaw() {
        when(courtAreaOfLawSpoeRepository.findAll()).thenReturn(COURT_SPOE_AREA_OF_LAWS);

        assertThat(adminCourtSpoeAreasOfLawService.getAllSpoeAreasOfLaw())
            .hasSize(COURT_AREAS_OF_LAW_COUNT)
            .first()
            .isInstanceOf(AreaOfLaw.class);
    }

    @Test
    void shouldReturnCourtSpoeAreasOfLaw() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtAreaOfLawSpoeRepository.getAllByCourtId(anyInt())).thenReturn(COURT_SPOE_AREA_OF_LAWS);

        assertThat(adminCourtSpoeAreasOfLawService.getCourtSpoeAreasOfLawBySlug(COURT_SLUG))
            .hasSize(COURT_AREAS_OF_LAW_COUNT)
            .first()
            .isInstanceOf(AreaOfLaw.class);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingCourtSpoeAreasOfLawForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());
        when(courtAreaOfLawSpoeRepository.getAllByCourtId(anyInt())).thenReturn(COURT_SPOE_AREA_OF_LAWS);

        assertThatThrownBy(() -> adminCourtSpoeAreasOfLawService.getCourtSpoeAreasOfLawBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldUpdateCourtSpoeAreasOfLaw() throws IOException {
        final String expectedJson = getResourceAsJson(TEST_COURT_AREAS_OF_LAW_PATH);
        final List<AreaOfLaw> areasOfLaw = asList(OBJECT_MAPPER.readValue(expectedJson, AreaOfLaw[].class));

        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtAreaOfLawSpoeRepository.getAllByCourtId(anyInt())).thenReturn(COURT_SPOE_AREA_OF_LAWS);
        when(courtAreaOfLawSpoeRepository.saveAll(any())).thenReturn(COURT_SPOE_AREA_OF_LAWS);

        final List<AreaOfLaw> courtAreasOfLawResult =
            adminCourtSpoeAreasOfLawService.updateSpoeAreasOfLawForCourt(COURT_SLUG, areasOfLaw);
        verify(courtAreaOfLawSpoeRepository).deleteAllByCourtId(court.getId());
        verify(courtAreaOfLawSpoeRepository).saveAll(anyIterable());

        assertThat(courtAreasOfLawResult)
            .hasSize(COURT_AREAS_OF_LAW_COUNT)
            .containsExactlyElementsOf(areasOfLaw);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court spoe areas of law",
                                                           COURT_SPOE_AREA_OF_LAWS
                                                               .stream()
                                                               .map(aol -> new AreaOfLaw(aol.getAreaOfLaw(), true))
                                                               .collect(toList()),
                                                           courtAreasOfLawResult, COURT_SLUG);
    }

    @Test
    void shouldNotUpdateCourtSpoeAreasOfLawWhenSlugNotFound() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());
        when(courtAreaOfLawSpoeRepository.getAllByCourtId(anyInt())).thenReturn(new ArrayList<>());

        assertThatThrownBy(() -> adminCourtSpoeAreasOfLawService.updateSpoeAreasOfLawForCourt(COURT_SLUG, new ArrayList<>()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }
}
