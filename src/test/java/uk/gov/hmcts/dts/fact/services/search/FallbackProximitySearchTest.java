package uk.gov.hmcts.dts.fact.services.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FallbackProximitySearch.class)
class FallbackProximitySearchTest {

    private static final double LAT = 52.1;
    private static final double LON = 0.7;
    private static final String AREA_OF_LAW = "Divorce";

    @Autowired
    private FallbackProximitySearch fallbackProximitySearch;

    @MockitoBean
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @Test
    void shouldReturnFallbackSearchResultsIfCourtsEmpty() {

        final MapitData mapitData = mock(MapitData.class);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(courtWithDistanceRepository.findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW, true)).thenReturn(courts);

        final List<CourtWithDistance> courtWithDistances = fallbackProximitySearch.fallbackIfEmpty(emptyList(), AREA_OF_LAW, true, mapitData);

        assertThat(courtWithDistances).isEqualTo(courts);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW, true);
    }

    @Test
    void shouldReturnCourtsIfNotEmpty() {

        final MapitData mapitData = mock(MapitData.class);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        final List<CourtWithDistance> courtWithDistances = fallbackProximitySearch.fallbackIfEmpty(courts, AREA_OF_LAW, true, mapitData);

        assertThat(courtWithDistances).isEqualTo(courts);
        verifyNoInteractions(courtWithDistanceRepository);
    }
}
