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
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtAreasOfLawService.class)
public class AdminCourtAreasOfLawServiceTest {

    private static final String COURT_SLUG = "some slug";
    private static final int COURT_AREAS_OF_LAW_COUNT = 3;
    private static final List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> COURT_AREAS_OF_LAW = new ArrayList<>();
    private static final String NOT_FOUND = "Not found: ";


    @MockBean
    private CourtRepository courtRepository;

    @Mock
    private Court court;

    @Autowired
    private AdminCourtAreasOfLawService adminCourtAreasOfLawService;

    @BeforeAll
    static void setUp() {

        uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLawOne = new uk.gov.hmcts.dts.fact.entity.AreaOfLaw();
        areaOfLawOne.setId(1);
        areaOfLawOne.setName("Area of law one");
        uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLawTwo = new uk.gov.hmcts.dts.fact.entity.AreaOfLaw();
        areaOfLawTwo.setId(2);
        areaOfLawTwo.setName("Area of law two");
        uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLawThree = new uk.gov.hmcts.dts.fact.entity.AreaOfLaw();
        areaOfLawThree.setId(3);
        areaOfLawThree.setName("Area of law three");

        COURT_AREAS_OF_LAW.add(areaOfLawOne);
        COURT_AREAS_OF_LAW.add(areaOfLawTwo);
        COURT_AREAS_OF_LAW.add(areaOfLawThree);
    }

    @Test
    void shouldReturnCourtAreasOfLaw() {
        when(court.getAreasOfLaw()).thenReturn(COURT_AREAS_OF_LAW);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        assertThat(adminCourtAreasOfLawService.getCourtAreasOfLawBySlug(COURT_SLUG))
            .hasSize(COURT_AREAS_OF_LAW_COUNT)
            .first()
            .isInstanceOf(AreaOfLaw.class);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingCourtLocalAuthoritiesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtAreasOfLawService.getCourtAreasOfLawBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }
}
