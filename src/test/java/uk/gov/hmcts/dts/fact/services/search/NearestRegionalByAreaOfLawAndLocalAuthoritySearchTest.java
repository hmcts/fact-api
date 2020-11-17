package uk.gov.hmcts.dts.fact.services.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.mapit.MapitData;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.repositories.CourtWithDistanceRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = NearestRegionalByAreaOfLawAndLocalAuthoritySearch.class)
class NearestRegionalByAreaOfLawAndLocalAuthoritySearchTest {

    private static final String AREA_OF_LAW_NAME = "AreaOfLawName";
    private static final String JE2_4BA = "JE2 4BA";

    @Autowired
    private NearestRegionalByAreaOfLawAndLocalAuthoritySearch nearestRegionalByAreaOfLawAndLocalAuthoritySearch;

    @MockBean
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @Test
    void shouldSearchByAreaOfLawWithPostcode() {

        final MapitData mapitData = mock(MapitData.class);

        final List<CourtWithDistance> courts = singletonList(mock(CourtWithDistance.class));

        when(mapitData.getLocalAuthority()).thenReturn(Optional.of("Suffolk County Council"));
        when(courtWithDistanceRepository.findNearestRegionalByAreaOfLawAndLocalAuthority(anyDouble(), anyDouble(), anyString(), anyString()))
            .thenReturn(courts);

        final List<CourtReferenceWithDistance> results = nearestRegionalByAreaOfLawAndLocalAuthoritySearch.search(
            mapitData,
            "OX2 1RZ",
            AREA_OF_LAW_NAME
        );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
    }

    @Test
    void shouldReturnEmptyListIfNoCoordinates() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of("Suffolk County Council"));

        final List<CourtReferenceWithDistance> results = nearestRegionalByAreaOfLawAndLocalAuthoritySearch.search(
            mock(MapitData.class),
            JE2_4BA,
            AREA_OF_LAW_NAME
        );

        assertThat(results.isEmpty()).isTrue();
    }

    @Test
    void shouldReturnEmptyListIfNoLocalAuthority() {

        final MapitData mapitData = mock(MapitData.class);
        when(mapitData.getLocalAuthority()).thenReturn(empty());

        final List<CourtReferenceWithDistance> results = nearestRegionalByAreaOfLawAndLocalAuthoritySearch.search(
            mapitData,
            JE2_4BA,
            AREA_OF_LAW_NAME
        );

        assertThat(results).isEmpty();
    }
}