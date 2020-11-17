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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = NearestCourtsByCourtPostcodeAndAreaOfLawSearch.class)
class NearestCourtsByCourtPostcodeAndAreaOfLawSearchTest {

    private static final String AREA_OF_LAW_NAME = "AreaOfLawName";
    private static final String JE2_4BA = "JE2 4BA";

    @Autowired
    private NearestCourtsByCourtPostcodeAndAreaOfLawSearch nearestCourtsByCourtPostcodeAndAreaOfLawSearch;

    @MockBean
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @Test
    void shouldSearchByAreaOfLawAndCourtPostcode() {
        final MapitData mapitData = mock(MapitData.class);

        final List<CourtWithDistance> courts = asList(
            mock(CourtWithDistance.class),
            mock(CourtWithDistance.class),
            mock(CourtWithDistance.class));

        when(courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(
            anyDouble(),
            anyDouble(),
            anyString(),
            anyString()
        )).thenReturn(
            courts);

        final List<CourtReferenceWithDistance> results = nearestCourtsByCourtPostcodeAndAreaOfLawSearch.search(
            mapitData,
            "OX2 1RZ",
            AREA_OF_LAW_NAME
        );
        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0)).isInstanceOf(CourtReferenceWithDistance.class);
    }

    @Test
    void shouldReturnEmptyListForFilterSearchByAreaOfLawAndCourtPostcodeIfNoCoordinates() {

        final List<CourtReferenceWithDistance> results = nearestCourtsByCourtPostcodeAndAreaOfLawSearch.search(
            mock(MapitData.class),
            JE2_4BA,
            AREA_OF_LAW_NAME
        );

        assertThat(results.isEmpty()).isTrue();
    }
}