package uk.gov.hmcts.dts.fact.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.CourtWithDistance;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CourtRepositoryTest {

    @Autowired
    private CourtRepository courtRepository;

    @Test
    void shouldFindExistingCourt() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        OldCourt expected = mapper.readValue(
            Paths.get("src/integrationTest/resources/deprecated/aylesbury-magistrates-court-and-family-court.json").toFile(),
            OldCourt.class
        );

        Optional<Court> result = courtRepository.findBySlug("aylesbury-magistrates-court-and-family-court");
        assertThat(result).isPresent();
        OldCourt court = new OldCourt(result.get());
        assertThat(court.getSlug()).isEqualTo(expected.getSlug());
        assertThat(court.getAreasOfLaw()).isEqualTo(expected.getAreasOfLaw());
        assertThat(court.getContacts()).isEqualTo(expected.getContacts());
        assertThat(court.getAddresses()).isEqualTo(expected.getAddresses());
        assertThat(court.getAddresses().get(0).getAddressType()).isNotEqualTo("");
        assertThat(court.getEmails()).isEqualTo(expected.getEmails());
        assertThat(court.getCourtTypes()).isEqualTo(expected.getCourtTypes());
        assertThat(court.getFacilities()).isEqualTo(expected.getFacilities());
        assertThat(court.getOpeningTimes()).isEqualTo(expected.getOpeningTimes());
    }

    @Test
    void shouldNotFindNonExistentCourt() {
        Optional<Court> court = courtRepository.findBySlug("non-existent-slug");
        assertThat(court).isEmpty();
    }

    @Test
    void shouldHandleEmptyString() {
        Optional<Court> court = courtRepository.findBySlug("");
        assertThat(court).isEmpty();
    }

    @Test
    void shouldHandleNull() {
        Optional<Court> court = courtRepository.findBySlug(null);
        assertThat(court).isEmpty();
    }

    @Test
    void shouldFindCourtsByQuery() throws IOException {
        final String query = "Administrative Court";

        ObjectMapper mapper = new ObjectMapper();

        List<CourtReference> expected = Arrays.asList(mapper.readValue(
            Paths.get("src/integrationTest/resources/courts.json").toFile(),
            CourtReference[].class
        ));

        List<Court> result = courtRepository.queryBy(query);
        assertThat(result.size()).isGreaterThanOrEqualTo(1);
        CourtReference court =
            new CourtReference(result.get(0).getName(), result.get(0).getSlug());
        assertThat(court.getName()).isEqualTo(expected.get(0).getName());
        assertThat(court.getSlug()).isEqualTo(expected.get(0).getSlug());
    }

    @Test
    void shouldFindCourtsByNameOrAddress() {
        final String query = "Oxford";
        List<Court> result = courtRepository.queryBy(query);
        assertThat(result.size()).isGreaterThanOrEqualTo(1);
        assertThat(result.stream().anyMatch(r -> r.getName().contains(query)));
        assertThat(result.stream().filter(r -> !r.getName().contains(query)).map(Court::getAddresses)
                       .flatMap(Collection::stream).map(CourtAddress::getAddress).anyMatch(a -> a.contains(query)));

    }

    @Test
    void shouldNotFindNonExistentCourtByQuery() {
        List<Court> result = courtRepository.queryBy("This does not exist");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindNearest() {
        List<CourtWithDistance> result = courtRepository.findNearest(51.8, -1.3);
        final List<CourtWithDistance> collect = result.stream().filter(r -> null != r.getDistance()).collect(Collectors.toList());
        assertThat(collect).isSortedAccordingTo(Comparator.comparing(CourtWithDistance::getDistance));
    }
}
