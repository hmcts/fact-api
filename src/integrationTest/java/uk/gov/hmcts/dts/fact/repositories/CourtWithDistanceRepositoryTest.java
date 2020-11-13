package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class CourtWithDistanceRepositoryTest {

    @Autowired
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @Test
    void shouldFindNearestTen() {
        final List<CourtWithDistance> result = courtWithDistanceRepository.findNearestTen(51.8, -1.3);
        final List<CourtWithDistance> collect = result.stream().filter(r -> null != r.getDistance()).collect(Collectors.toList());
        assertThat(collect).isSortedAccordingTo(Comparator.comparing(CourtWithDistance::getDistance));
    }

    @Test
    void shouldFindNearestTenByAreaOfLaw() {
        final List<CourtWithDistance> result = courtWithDistanceRepository.findNearestTenByAreaOfLaw(51.8, -1.3, "Tax");
        final List<CourtWithDistance> collect = result.stream().filter(r -> null != r.getDistance()).collect(Collectors.toList());
        assertThat(collect).isSortedAccordingTo(Comparator.comparing(CourtWithDistance::getDistance));
        assertThat(result.get(0).getAreasOfLaw().stream().anyMatch("Tax"::equals));
    }

    @Test
    void shouldFindNearestTenByAreaOfLawAndPostcode() {
        final List<CourtWithDistance> result = courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(51.8, -1.3, "Money claims", "NW62HH");
        final List<CourtWithDistance> collect = result.stream().filter(r -> null != r.getDistance()).collect(Collectors.toList());
        assertThat(collect).isSortedAccordingTo(Comparator.comparing(CourtWithDistance::getDistance));
        assertThat(result.get(0).getAreasOfLaw().stream().anyMatch("Money claims"::equals));
    }
}
