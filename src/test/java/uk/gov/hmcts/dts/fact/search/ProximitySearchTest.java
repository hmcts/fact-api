package uk.gov.hmcts.dts.fact.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;
import uk.gov.hmcts.dts.fact.services.search.ProximitySearch;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ProximitySearchTest {

    @Mock
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @Test
    void testSearchWithReturnsCourts() {
        final ProximitySearch proximitySearch = new ProximitySearch(courtWithDistanceRepository);
        final MapitData mapitData = new MapitData();
        mapitData.setLat(10.0);
        mapitData.setLon(10.1);
        when(courtWithDistanceRepository.findNearestTen(10.0, 10.1)).thenReturn(asList(
            mock(CourtWithDistance.class), mock(CourtWithDistance.class)));
        List<CourtWithDistance> cwd = proximitySearch.searchWith(mapitData);
        assertEquals(2, cwd.size());
        assertThat(cwd.get(0)).isInstanceOf(CourtWithDistance.class);
        assertThat(cwd.get(1)).isInstanceOf(CourtWithDistance.class);
    }
}
