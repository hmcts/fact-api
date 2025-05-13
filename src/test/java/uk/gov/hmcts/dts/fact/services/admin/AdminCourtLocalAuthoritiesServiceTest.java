package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtLocalAuthorityAreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.LocalAuthority;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtLocalAuthorityAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtLocalAuthoritiesService.class)
class AdminCourtLocalAuthoritiesServiceTest {

    private static final int LOCAL_AUTHORITIES_COUNT = 3;
    private static final List<CourtLocalAuthorityAreaOfLaw> COURT_LOCAL_AUTHORITIES = new ArrayList<>();
    private static final List<AreaOfLaw> COURT_AREAS_OF_LAW = new ArrayList<>();
    private static final String COURT_SLUG = "some slug";
    private static final String AREA_OF_LAW = "Area of law one";
    private static final String NON_EXISTENT_AREA_OF_LAW = "Non existent area of law";
    private static final String NOT_FOUND = "Not found: ";

    private static final List<uk.gov.hmcts.dts.fact.model.admin.LocalAuthority> EXPECTED_COURT_LOCAL_AUTHORITIES = Arrays.asList(
        new uk.gov.hmcts.dts.fact.model.admin.LocalAuthority(1,"localAuthority1"),
        new uk.gov.hmcts.dts.fact.model.admin.LocalAuthority(2,"localAuthority2"),
        new uk.gov.hmcts.dts.fact.model.admin.LocalAuthority(3,"localAuthority3"));

    @MockitoBean
    private CourtRepository courtRepository;

    @MockitoBean
    private CourtLocalAuthorityAreaOfLawRepository courtLocalAuthorityAreaOfLawRepository;

    @MockitoBean
    private AdminAuditService adminAuditService;

    @Mock
    private Court court;

    @Autowired
    private AdminCourtLocalAuthoritiesService adminCourtLocalAuthoritiesService;

    @BeforeAll
    static void setUp() {

        AreaOfLaw areaOfLawOne = new AreaOfLaw();
        areaOfLawOne.setId(1);
        areaOfLawOne.setName("Area of law one");
        AreaOfLaw areaOfLawTwo = new AreaOfLaw();
        areaOfLawTwo.setId(2);
        areaOfLawTwo.setName("Area of law two");
        AreaOfLaw areaOfLawThree = new AreaOfLaw();
        areaOfLawThree.setId(3);
        areaOfLawThree.setName("Area of law three");

        COURT_AREAS_OF_LAW.add(areaOfLawOne);
        COURT_AREAS_OF_LAW.add(areaOfLawTwo);
        COURT_AREAS_OF_LAW.add(areaOfLawThree);

        final LocalAuthority localAuthority1 = new LocalAuthority(1,"localAuthority1");
        final LocalAuthority localAuthority2 = new LocalAuthority(2,"localAuthority2");
        final LocalAuthority localAuthority3 = new LocalAuthority(3,"localAuthority3");

        CourtLocalAuthorityAreaOfLaw courtLocalAuthorityAreaOfLaw1 = new CourtLocalAuthorityAreaOfLaw();
        courtLocalAuthorityAreaOfLaw1.setCourt(mock(Court.class));
        courtLocalAuthorityAreaOfLaw1.setLocalAuthority(localAuthority1);
        courtLocalAuthorityAreaOfLaw1.setAreaOfLaw(areaOfLawOne);

        CourtLocalAuthorityAreaOfLaw courtLocalAuthorityAreaOfLaw2 = new CourtLocalAuthorityAreaOfLaw();
        courtLocalAuthorityAreaOfLaw2.setCourt(mock(Court.class));
        courtLocalAuthorityAreaOfLaw2.setLocalAuthority(localAuthority2);
        courtLocalAuthorityAreaOfLaw2.setAreaOfLaw(areaOfLawOne);

        CourtLocalAuthorityAreaOfLaw courtLocalAuthorityAreaOfLaw3 = new CourtLocalAuthorityAreaOfLaw();
        courtLocalAuthorityAreaOfLaw3.setCourt(mock(Court.class));
        courtLocalAuthorityAreaOfLaw3.setLocalAuthority(localAuthority3);
        courtLocalAuthorityAreaOfLaw3.setAreaOfLaw(areaOfLawOne);

        COURT_LOCAL_AUTHORITIES.add(courtLocalAuthorityAreaOfLaw1);
        COURT_LOCAL_AUTHORITIES.add(courtLocalAuthorityAreaOfLaw2);
        COURT_LOCAL_AUTHORITIES.add(courtLocalAuthorityAreaOfLaw3);
    }

    @Test
    void shouldReturnCourtLocalAuthorities() {
        when(courtLocalAuthorityAreaOfLawRepository.findByCourtId(any())).thenReturn(COURT_LOCAL_AUTHORITIES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        assertThat(adminCourtLocalAuthoritiesService.getCourtLocalAuthoritiesBySlugAndAreaOfLaw(COURT_SLUG, AREA_OF_LAW))
            .hasSize(LOCAL_AUTHORITIES_COUNT)
            .first()
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.LocalAuthority.class);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingCourtLocalAuthoritiesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtLocalAuthoritiesService.getCourtLocalAuthoritiesBySlugAndAreaOfLaw(COURT_SLUG, AREA_OF_LAW))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldUpdateCourtLocalAuthorities() {
        when(court.getAreasOfLaw()).thenReturn(COURT_AREAS_OF_LAW);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtRepository.save(court)).thenReturn(court);
        when(courtLocalAuthorityAreaOfLawRepository.findByCourtId(any())).thenReturn(COURT_LOCAL_AUTHORITIES);
        when(courtLocalAuthorityAreaOfLawRepository.saveAll(any())).thenReturn(COURT_LOCAL_AUTHORITIES);

        List<uk.gov.hmcts.dts.fact.model.admin.LocalAuthority> results =
            adminCourtLocalAuthoritiesService.updateCourtLocalAuthority(COURT_SLUG, AREA_OF_LAW, EXPECTED_COURT_LOCAL_AUTHORITIES);
        assertThat(results)
            .hasSize(LOCAL_AUTHORITIES_COUNT)
            .containsExactlyElementsOf(EXPECTED_COURT_LOCAL_AUTHORITIES);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court local authorities",
                                                           COURT_LOCAL_AUTHORITIES
                                                               .stream()
                                                               .map(la -> new uk.gov.hmcts.dts.fact.model.admin.LocalAuthority(la.getLocalAuthority().getId(), la.getLocalAuthority().getName()))
                                                               .collect(toList()),
                                                           results, COURT_SLUG);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingCourtLocalAuthoritiesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtLocalAuthoritiesService.updateCourtLocalAuthority(COURT_SLUG, AREA_OF_LAW, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingCourtLocalAuthoritiesForNonExistentAreaOfLaw() {
        when(court.getAreasOfLaw()).thenReturn(COURT_AREAS_OF_LAW);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        assertThatThrownBy(() -> adminCourtLocalAuthoritiesService.updateCourtLocalAuthority(COURT_SLUG, NON_EXISTENT_AREA_OF_LAW, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + NON_EXISTENT_AREA_OF_LAW);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }
}
