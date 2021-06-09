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
import uk.gov.hmcts.dts.fact.entity.*;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtLocalAuthorityRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.LocalAuthorityRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtLocalAuthoritiesService.class)
public class AdminCourtLocalAuthoritiesServiceTest {


    private static final int LOCAL_AUTHORITIES_COUNT = 3;
    private static final int COURT_LOCAL_AUTHORITIES_COUNT = 1;
    private static final List<CourtLocalAuthority> COURT_LOCAL_AUTHORITIES = new ArrayList<>();
    private static final String COURT_SLUG = "some slug";
    private static final String NOT_FOUND = "Not found: ";

    private static final List<LocalAuthority> EXPECTED_LOCAL_AUTHORITIES_ENTITY = Arrays.asList(
        new LocalAuthority(1,"test1"),
        new LocalAuthority(2,"test2"),
        new LocalAuthority(3,"test3")
    );

    private static final List<uk.gov.hmcts.dts.fact.model.admin.CourtLocalAuthority> EXPECTED_COURT_LOCAL_AUTHORITIES = Arrays.asList(
        new uk.gov.hmcts.dts.fact.model.admin.CourtLocalAuthority(0,0,0));


    @MockBean
    private LocalAuthorityRepository localAuthorityRepository;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtLocalAuthorityRepository courtLocalAuthorityRepository;

    @Mock
    private Court court;

    @Autowired
    private AdminCourtLocalAuthoritiesService adminCourtLocalAuthoritiesService;

    @BeforeAll
    static void setUp() {
        for (int i = 0; i < COURT_LOCAL_AUTHORITIES_COUNT; i++) {
            final CourtLocalAuthority courtLocalAuthority = mock(CourtLocalAuthority.class);
            COURT_LOCAL_AUTHORITIES.add(courtLocalAuthority);
        }

        when(COURT_LOCAL_AUTHORITIES.get(0).getCourt())
            .thenReturn(mock(Court.class));
        when(COURT_LOCAL_AUTHORITIES.get(0).getAreaOfLaw())
            .thenReturn(mock(AreaOfLaw.class));
        when(COURT_LOCAL_AUTHORITIES.get(0).getLocalAuthority())
            .thenReturn(mock(LocalAuthority.class));

    }

    @Test
    void shouldReturnAllLocalAuthorities() {
        when(localAuthorityRepository.findAll()).thenReturn(EXPECTED_LOCAL_AUTHORITIES_ENTITY);

        assertThat(adminCourtLocalAuthoritiesService.getAllLocalAuthorities())
            .hasSize(LOCAL_AUTHORITIES_COUNT)
            .first()
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.LocalAuthority.class);
    }

    @Test
    void shouldReturnCourtLocalAuthorities() {
        when(courtLocalAuthorityRepository.findByCourtId(any())).thenReturn(COURT_LOCAL_AUTHORITIES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        assertThat(adminCourtLocalAuthoritiesService.getCourtLocalAuthoritiesBySlug(COURT_SLUG))
            .hasSize(COURT_LOCAL_AUTHORITIES_COUNT)
            .first()
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.CourtLocalAuthority.class);
    }



    @Test
    void shouldReturnNotFoundWhenRetrievingCourtLocalAuthoritiesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtLocalAuthoritiesService.getCourtLocalAuthoritiesBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldUpdateCourtLocalAuthorities() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtRepository.save(court)).thenReturn(court);
        when(courtLocalAuthorityRepository.findByCourtId(any())).thenReturn(COURT_LOCAL_AUTHORITIES);
        when(courtLocalAuthorityRepository.saveAll(any())).thenReturn(COURT_LOCAL_AUTHORITIES);

        assertThat(adminCourtLocalAuthoritiesService.updateCourtLocalAuthority(COURT_SLUG,
                                                                               EXPECTED_COURT_LOCAL_AUTHORITIES
        ))
            .hasSize(COURT_LOCAL_AUTHORITIES_COUNT)
            .containsExactlyElementsOf(EXPECTED_COURT_LOCAL_AUTHORITIES);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingCourtTypesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtLocalAuthoritiesService.updateCourtLocalAuthority(COURT_SLUG, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }



}
