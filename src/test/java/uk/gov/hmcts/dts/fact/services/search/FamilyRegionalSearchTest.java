package uk.gov.hmcts.dts.fact.services.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FamilyRegionalSearch.class)
class FamilyRegionalSearchTest {

    private static final double LAT = 52.1;
    private static final double LON = 0.7;
    private static final String AREA_OF_LAW = "Divorce";
    private static final String JE2_4BA = "JE2 4BA";
    private static final String LOCAL_AUTHORITY_NAME = "Suffolk County Council";

    @Autowired
    private FamilyRegionalSearch familyRegionalSearch;

    @MockBean
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @Test
    void shouldReturnFamilyRegionalSearchResults() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceArea serviceArea = new ServiceArea();
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
        when(courtWithDistanceRepository.findNearestRegionalByAreaOfLawAndLocalAuthority(LAT, LON, AREA_OF_LAW, LOCAL_AUTHORITY_NAME))
            .thenReturn(courts);

        final List<CourtWithDistance> courtWithDistances = familyRegionalSearch.searchWith(serviceArea, mapitData, JE2_4BA);

        assertThat(courtWithDistances).isEqualTo(courts);
        verify(courtWithDistanceRepository).findNearestRegionalByAreaOfLawAndLocalAuthority(LAT, LON, AREA_OF_LAW, LOCAL_AUTHORITY_NAME);
    }

    @Test
    void shouldReturnFallbackSearchResultsIfSearchIsEmpty() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceArea serviceArea = new ServiceArea();
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of(LOCAL_AUTHORITY_NAME));
        when(courtWithDistanceRepository.findNearestRegionalByAreaOfLawAndLocalAuthority(LAT, LON, AREA_OF_LAW, LOCAL_AUTHORITY_NAME))
            .thenReturn(emptyList());
        when(courtWithDistanceRepository.findNearestRegionalByAreaOfLaw(LAT, LON, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtWithDistance> courtWithDistances = familyRegionalSearch.searchWith(serviceArea, mapitData, JE2_4BA);

        assertThat(courtWithDistances).isEqualTo(courts);
        verify(courtWithDistanceRepository).findNearestRegionalByAreaOfLawAndLocalAuthority(LAT, LON, AREA_OF_LAW, LOCAL_AUTHORITY_NAME);
        verify(courtWithDistanceRepository).findNearestRegionalByAreaOfLaw(LAT, LON, AREA_OF_LAW);
    }

    @Test
    void shouldReturnFallbackSearchResultsIf() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceArea serviceArea = new ServiceArea();
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(mapitData.getLocalAuthority()).thenReturn(empty());
        when(courtWithDistanceRepository.findNearestRegionalByAreaOfLaw(LAT, LON, AREA_OF_LAW)).thenReturn(courts);

        final List<CourtWithDistance> courtWithDistances = familyRegionalSearch.searchWith(serviceArea, mapitData, JE2_4BA);

        assertThat(courtWithDistances).isEqualTo(courts);
        verify(courtWithDistanceRepository).findNearestRegionalByAreaOfLaw(LAT, LON, AREA_OF_LAW);
        verifyNoMoreInteractions(courtWithDistanceRepository);
    }
}