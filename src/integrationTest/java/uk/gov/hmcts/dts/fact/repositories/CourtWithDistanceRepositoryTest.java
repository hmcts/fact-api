package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class CourtWithDistanceRepositoryTest {

    private static final String AREA_OF_LAW = "Divorce";
    private static final double LON = -0.25;
    private static final double LAT = 50.84;

    @Autowired
    private CourtWithDistanceRepository courtWithDistanceRepository;

    @Test
    void shouldFindNearestTen() {
        final List<CourtWithDistance> result = courtWithDistanceRepository.findNearestTen(51.8, -1.3, true);
        final List<CourtWithDistance> collect = result.stream().filter(r -> null != r.getDistance()).collect(Collectors.toList());
        assertThat(collect).isSortedAccordingTo(Comparator.comparing(CourtWithDistance::getDistance));
    }

    @Test
    void shouldFindNearestTenByAreaOfLaw() {
        final List<CourtWithDistance> result = courtWithDistanceRepository.findNearestTenByAreaOfLaw(51.8, -1.3, "Tax", true);
        final List<CourtWithDistance> collect = result.stream().filter(r -> null != r.getDistance()).collect(Collectors.toList());
        assertThat(collect).isSortedAccordingTo(Comparator.comparing(CourtWithDistance::getDistance));
        assertThat(result.get(0).getAreasOfLaw().stream().map(AreaOfLaw::getName).anyMatch("Tax"::equals));
    }

    @Test
    void shouldFindNearestTenByAreaOfLawAndPostcode() {
        final List<CourtWithDistance> result = courtWithDistanceRepository.findNearestTenByAreaOfLawAndCourtPostcode(51.8, -1.3, "Money claims", "NW62HH");
        final List<CourtWithDistance> collect = result.stream().filter(r -> null != r.getDistance()).collect(Collectors.toList());
        assertThat(collect).isSortedAccordingTo(Comparator.comparing(CourtWithDistance::getDistance));
        assertThat(result.get(0).getAreasOfLaw().stream().map(AreaOfLaw::getName).anyMatch("Money claims"::equals));
    }


    @Test
    void shouldFindNearestTenByAreaOfLawAndLocalAuthority() {
        final List<CourtWithDistance> result = courtWithDistanceRepository.findNearestTenByAreaOfLawAndLocalAuthority(
            LAT,
            LON,
            "Adoption",
            "Brighton and Hove City Council",
            true);

        assertThat(result).isSortedAccordingTo(Comparator.comparing(CourtWithDistance::getDistance));
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getAreasOfLaw().stream().map(AreaOfLaw::getName).anyMatch("Adoption"::equals));
        assertThat(result.get(1).getAreasOfLaw().stream().map(AreaOfLaw::getName).anyMatch("Adoption"::equals));
    }

    @Test
    void shouldFindNearestRegionalByAreaOfLawAndLocalAuthority() {
        final List<CourtWithDistance> result = courtWithDistanceRepository.findNearestRegionalByAreaOfLawAndLocalAuthority(
            LAT,
            LON,
            AREA_OF_LAW,
            "Suffolk County Council"
        );

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getAreasOfLaw().stream().map(AreaOfLaw::getName).anyMatch(AREA_OF_LAW::equals));
    }

    @Test
    void shouldFindNearestRegionalByAreaOfLaw() {
        final List<CourtWithDistance> result = courtWithDistanceRepository.findNearestRegionalByAreaOfLaw(
            LAT,
            LON,
            AREA_OF_LAW
        );

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getAreasOfLaw().stream().map(AreaOfLaw::getName).anyMatch(AREA_OF_LAW::equals));
    }
}
