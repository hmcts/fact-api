package uk.gov.hmcts.dts.fact.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.*;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CourtRepositoryTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private CourtRepository courtRepository;

    @Test
    void shouldFindExistingCourt() throws IOException {

        final OldCourt expected = OBJECT_MAPPER.readValue(
            Paths.get("src/integrationTest/resources/deprecated/aylesbury-magistrates-court-and-family-court.json").toFile(),
            OldCourt.class
        );

        final Optional<Court> result = courtRepository.findBySlug("aylesbury-magistrates-court-and-family-court");
        assertThat(result).isPresent();
        final OldCourt court = new OldCourt(result.get());
        assertThat(court.getSlug()).isEqualTo(expected.getSlug());
        assertThat(court.getAreasOfLaw()).isEqualTo(expected.getAreasOfLaw());
        assertThat(court.getContacts()).isEqualTo(expected.getContacts());
        assertThat(court.getAddresses()).isEqualTo(expected.getAddresses());
        assertThat(court.getAddresses().get(0).getAddressType()).isNotEqualTo("");
        assertThat(court.getEmails()).isEqualTo(expected.getEmails());
        assertThat(court.getCourtTypes()).isEqualTo(expected.getCourtTypes());
        assertThat(court.getFacilities()).containsExactlyInAnyOrderElementsOf(expected.getFacilities());
        assertThat(court.getOpeningTimes()).isEqualTo(expected.getOpeningTimes());
    }

    @Test
    void shouldNotFindNonExistentCourt() {
        final Optional<Court> court = courtRepository.findBySlug("non-existent-slug");
        assertThat(court).isEmpty();
    }

    @Test
    void shouldHandleEmptyString() {
        final Optional<Court> court = courtRepository.findBySlug("");
        assertThat(court).isEmpty();
    }

    @Test
    void shouldHandleNull() {
        final Optional<Court> court = courtRepository.findBySlug(null);
        assertThat(court).isEmpty();
    }

    @Test
    void shouldFindCourtsByQuery() throws IOException {
        final String query = "Administrative Court";

        final List<CourtReference> expected = Arrays.asList(OBJECT_MAPPER.readValue(
            Paths.get("src/test/resources/courts.json").toFile(),
            CourtReference[].class
        ));

        final List<Court> result = courtRepository.queryBy(query);
        assertThat(result.size()).isGreaterThanOrEqualTo(1);
        final CourtReference court = new CourtReference(result.get(0));
        assertThat(court.getName()).isEqualTo(expected.get(0).getName());
        assertThat(court.getSlug()).isEqualTo(expected.get(0).getSlug());
    }

    @Test
    void shouldFindCourtsByNameOrAddress() {
        final String query = "Oxford";
        final List<Court> result = courtRepository.queryBy(query);
        assertThat(result.size()).isGreaterThanOrEqualTo(1);
        assertThat(result.stream().anyMatch(r -> r.getName().contains(query)));
        assertThat(result.stream()
            .filter(r -> !r.getName().contains(query))
            .map(Court::getAddresses)
            .flatMap(Collection::stream)
            .map(CourtAddress::getAddress)
            .anyMatch(a -> a.contains(query)));
    }

    @Test
    void shouldNotFindNonExistentCourtByQuery() {
        final List<Court> result = courtRepository.queryBy("This does not exist");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAllCourts() {
        final List<Court> result = courtRepository.findAll();
        assertThat(result.size()).isGreaterThanOrEqualTo(1);
    }
}
