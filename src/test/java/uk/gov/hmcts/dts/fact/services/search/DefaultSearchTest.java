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

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DefaultSearch.class)
class DefaultSearchTest {

    private static final double LAT = 52.1;
    private static final double LON = 0.7;
    private static final String AREA_OF_LAW = "Divorce";

    @Autowired
    private DefaultSearch defaultSearch;

    @MockBean
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @Test
    void shouldReturnDefaultSearchResults() {

        final MapitData mapitData = mock(MapitData.class);
        final ServiceArea serviceArea = new ServiceArea();
        final AreaOfLaw aol = new AreaOfLaw();
        aol.setName(AREA_OF_LAW);
        serviceArea.setAreaOfLaw(aol);
        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLat()).thenReturn(LAT);
        when(mapitData.getLon()).thenReturn(LON);
        when(courtWithDistanceRepository.findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW, true)).thenReturn(courts);

        final List<CourtWithDistance> courtWithDistances = defaultSearch.searchWith(serviceArea, mapitData, "JE2 4BA", true);

        assertThat(courtWithDistances).isEqualTo(courts);
        verify(courtWithDistanceRepository).findNearestTenByAreaOfLaw(LAT, LON, AREA_OF_LAW, true);
    }
}
