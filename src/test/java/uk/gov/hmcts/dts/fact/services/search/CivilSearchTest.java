package uk.gov.hmcts.dts.fact.services.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CivilSearch.class)
class CivilSearchTest {

    private static final double LAT = 52.1;
    private static final double LON = 0.7;
    private static final String AREA_OF_LAW = "Divorce";
    private static final String JE2_4BA = "JE2 4BA";
    private static final String JE2_4 = "JE2 4";
    private static final String JE2 = "JE2";
    private static final String JE = "JE";

    @Autowired
    private CivilSearch civilSearch;

    @MockitoBean
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @MockitoBean
    private FallbackProximitySearch fallbackProximitySearch;

    @Test
    void shouldReturnCivilSearchResults() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceArea serviceArea = new ServiceArea();
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA, true))
            .thenReturn(courts);
        when(fallbackProximitySearch.fallbackIfEmpty(courts, AREA_OF_LAW, true, mapitData)).thenReturn(courts);

        final List<CourtWithDistance> courtWithDistances = civilSearch.searchWith(serviceArea, mapitData, JE2_4BA, true);

        assertThat(courtWithDistances).isEqualTo(courts);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA, true);
        verify(fallbackProximitySearch).fallbackIfEmpty(courts, AREA_OF_LAW, true, mapitData);
        verify(courtWithDistanceRepository, times(1)).findNearestTenByAreaOfLawAndCourtPostcode(
            any(),
            any(),
            any(),
            any(),
            anyBoolean()
        );
    }

    @Test
    void shouldReturnCivilSearchResultsWithPartialPostcodeMinusUnitCode() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceArea serviceArea = new ServiceArea();
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA, true))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4, true))
            .thenReturn(courts);
        when(fallbackProximitySearch.fallbackIfEmpty(courts, AREA_OF_LAW, true, mapitData)).thenReturn(courts);

        final List<CourtWithDistance> courtWithDistances = civilSearch.searchWith(serviceArea, mapitData, JE2_4BA, true);

        assertThat(courtWithDistances).isEqualTo(courts);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA, true);
        verify(courtWithDistanceRepository, times(2)).findNearestTenByAreaOfLawAndCourtPostcode(
            any(),
            any(),
            any(),
            any(),
            anyBoolean()
        );
    }

    @Test
    void shouldReturnCivilSearchResultsWithPartialPostcodeOutcode() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceArea serviceArea = new ServiceArea();
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA, true))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4, true))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2, true))
            .thenReturn(courts);
        when(fallbackProximitySearch.fallbackIfEmpty(courts, AREA_OF_LAW, true, mapitData)).thenReturn(courts);

        final List<CourtWithDistance> courtWithDistances = civilSearch.searchWith(serviceArea, mapitData, JE2_4BA, true);

        assertThat(courtWithDistances).isEqualTo(courts);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA, true);
        verify(courtWithDistanceRepository, times(3)).findNearestTenByAreaOfLawAndCourtPostcode(
            any(),
            any(),
            any(),
            any(),
            anyBoolean()
        );
    }

    @Test
    void shouldReturnCivilSearchResultsWithPartialPostcodeArea() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceArea serviceArea = new ServiceArea();
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA, true))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4, true))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2, true))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE, true))
            .thenReturn(courts);
        when(fallbackProximitySearch.fallbackIfEmpty(courts, AREA_OF_LAW, true, mapitData)).thenReturn(courts);

        final List<CourtWithDistance> courtWithDistances = civilSearch.searchWith(serviceArea, mapitData, JE2_4BA, true);

        assertThat(courtWithDistances).isEqualTo(courts);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA, true);
        verify(courtWithDistanceRepository, times(4)).findNearestTenByAreaOfLawAndCourtPostcode(
            any(),
            any(),
            any(),
            any(),
            anyBoolean()
        );
    }

    @Test
    void shouldReturnCivilSearchResultsFallback() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceArea serviceArea = new ServiceArea();
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA, true))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4, true))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2, true))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE, true))
            .thenReturn(emptyList());
        when(fallbackProximitySearch.fallbackIfEmpty(emptyList(), AREA_OF_LAW, true, mapitData)).thenReturn(courts);

        final List<CourtWithDistance> courtWithDistances = civilSearch.searchWith(serviceArea, mapitData, JE2_4BA, true);

        assertThat(courtWithDistances).isEqualTo(courts);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLawAndCourtPostcode(LAT, LON, AREA_OF_LAW, JE2_4BA, true);
        verify(courtWithDistanceRepository, times(4)).findNearestTenByAreaOfLawAndCourtPostcode(
            any(),
            any(),
            any(),
            any(),
            anyBoolean()
        );
    }
}
