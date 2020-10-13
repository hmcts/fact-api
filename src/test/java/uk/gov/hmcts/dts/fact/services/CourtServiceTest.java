package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.SlugNotFoundException;
import uk.gov.hmcts.dts.fact.model.Court2;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.services.model.Coordinates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CourtService.class)
class CourtServiceTest {

    @Autowired
    CourtService courtService;

    @MockBean
    CourtRepository courtRepository;

    @MockBean
    MapitClient mapitClient;

    @Test
    void shouldThrowSlugNotFoundException() {
        when(courtRepository.findBySlug(any())).thenReturn(empty());
        assertThrows(SlugNotFoundException.class, () -> courtService.getCourtBySlug("some-slug"));
    }

    @Test
    void shouldReturnCourtObject() {
        final Court mock = mock(Court.class);
        when(courtRepository.findBySlug("some-slug")).thenReturn(Optional.of(mock));
        assertThat(courtService.getCourtBySlug("some-slug")).isInstanceOf(uk.gov.hmcts.dts.fact.model.Court.class);
    }

    @Test
    void shouldReturnListOfCourtReferenceObject() {
        final String query = "London";
        final Court mock = mock(Court.class);

        when(courtRepository.queryBy(query)).thenReturn(singletonList(mock));
        List<CourtReference> results = courtService.getCourtByNameOrAddressOrPostcodeOrTown(query);
        assertThat(results.get(0)).isInstanceOf(CourtReference.class);
        assertThat(results.size()).isGreaterThan(0);
    }

    @Test
    void shouldReturnListOfTenCourts() {

        final Coordinates mockCoordinates = mock(Coordinates.class);
        when(mapitClient.getCoordinates(any())).thenReturn(mockCoordinates);

        final List<uk.gov.hmcts.dts.fact.entity.Court2> courts = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            courts.add(mock(uk.gov.hmcts.dts.fact.entity.Court2.class));
        }

        when(courtRepository.findNearest(anyDouble(), anyDouble())).thenReturn(courts);

        final String postcode = "OX1 1RZ";

        List<Court2> results = courtService.getNearestCourtsByPostcode(postcode);
        assertThat(results.size()).isEqualTo(10);
    }

    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    void shouldFilterSearchByAreaOfLaw() {

        final Coordinates mockCoordinates = mock(Coordinates.class);
        when(mapitClient.getCoordinates(any())).thenReturn(mockCoordinates);

        final List<uk.gov.hmcts.dts.fact.entity.Court2> courts = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final uk.gov.hmcts.dts.fact.entity.Court2 mock = mock(uk.gov.hmcts.dts.fact.entity.Court2.class);
            final AreaOfLaw areaOfLaw = new AreaOfLaw();
            if (i % 4 == 0) {
                areaOfLaw.setName("AreaOfLawName");
            }
            List<AreaOfLaw> areasOfLaw = Arrays.asList(areaOfLaw);
            when(mock.getAreasOfLaw()).thenReturn(areasOfLaw);
            courts.add(mock);
        }

        when(courtRepository.findNearest(anyDouble(), anyDouble())).thenReturn(courts);

        final String postcode = "OX1 1RZ";

        List<Court2> results = courtService.getNearestCourtsByPostcodeAndAreaOfLaw(postcode, "AreaOfLawName");
        assertThat(results.size()).isEqualTo(5);
    }
}
