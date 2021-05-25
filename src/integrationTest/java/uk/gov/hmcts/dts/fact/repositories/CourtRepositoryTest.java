package uk.gov.hmcts.dts.fact.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.*;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.deprecated.OldCourt;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CourtRepositoryTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String ACCRINGTON_COURT_SLUG = "accrington-county-court";
    private static final String TEST_HOURS = "Test hours";
    private static final String TEST_NUMBER = "123";
    private static final String TEST_EMAIL = "test@test.com";

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private CourtContactRepository courtContactRepository;

    @Autowired
    private CourtEmailRepository courtEmailRepository;

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
    void shouldFindCourtsByPrefixAndDisplayedValueTrue() {
        final List<Court> result = courtRepository.findCourtBySlugStartingWithAndDisplayedOrderBySlugAsc("a", true);
        assertThat(result.size()).isGreaterThanOrEqualTo(1);
        Assertions.assertTrue(result.stream().allMatch(Court::getDisplayed));
        Assertions.assertTrue(result.stream().allMatch(c -> c.getName().charAt(0) == 'A'));
        Assertions.assertTrue(result.stream().allMatch(c -> c.getSlug().charAt(0) == 'a'));
    }

    @Test
    void shouldFindCourtsByPrefixAndDisplayedValueFalse() {
        final List<Court> result = courtRepository.findCourtBySlugStartingWithAndDisplayedOrderBySlugAsc("b", false);
        assertThat(result.size()).isGreaterThanOrEqualTo(1);
        Assertions.assertFalse(result.stream().allMatch(Court::getDisplayed));
        Assertions.assertTrue(result.stream().allMatch(c -> c.getName().charAt(0) == 'B'));
        Assertions.assertTrue(result.stream().allMatch(c -> c.getSlug().charAt(0) == 'b'));
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

    @Test
    void shouldUpdateTimestampWhenOpeningTimeIsUpdated() {
        final Optional<Court> courtOptional = courtRepository.findBySlug("aylesbury-magistrates-court-and-family-court");
        assertThat(courtOptional).isPresent();
        Court court = courtOptional.get();
        final long currentUpdateTime = court.getUpdatedAt().getTime();

        // Add a new opening time
        final OpeningTime newOpeningTime = new OpeningTime();
        newOpeningTime.setHours(TEST_HOURS);
        List<CourtOpeningTime> courtOpeningTimes = court.getCourtOpeningTimes();
        courtOpeningTimes.add(new CourtOpeningTime(court, newOpeningTime, 99));

        court.setCourtOpeningTimes(courtOpeningTimes);
        Court result = courtRepository.save(court);

        // Check the timestamp has been updated
        final long newUpdateTime = result.getUpdatedAt().getTime();
        assertThat(newUpdateTime).isGreaterThan(currentUpdateTime);

        // Remove the added opening time
        courtOpeningTimes = court.getCourtOpeningTimes();
        courtOpeningTimes.removeIf(o -> o.getOpeningTime().getHours().equals(TEST_HOURS));
        court.setCourtOpeningTimes(courtOpeningTimes);
        result = courtRepository.save(court);

        // Check the timestamp has been updated again
        assertThat(result.getUpdatedAt().getTime()).isGreaterThan(currentUpdateTime);
    }

    @Test
    void shouldUpdateTimestampWhenContactIsUpdated() {
        Optional<Court> courtOptional = courtRepository.findBySlug(ACCRINGTON_COURT_SLUG);
        assertThat(courtOptional).isPresent();
        Court court = courtOptional.get();
        final long currentUpdateTime = court.getUpdatedAt().getTime();

        List<CourtContact> courtContacts = court.getCourtContacts();
        final Contact contact = new Contact(null, TEST_NUMBER, "", "", false, 0);
        courtContacts.add(new CourtContact(court, contact));
        courtContactRepository.saveAll(courtContacts);

        // Check the timestamp has been updated
        courtOptional = courtRepository.findBySlug(ACCRINGTON_COURT_SLUG);
        assertThat(courtOptional).isPresent();
        assertThat(courtOptional.get().getUpdatedAt().getTime()).isGreaterThan(currentUpdateTime);
    }

    @Test
    void shouldUpdateTimestampWhenEmailIsUpdated() {
        Optional<Court> courtOptional = courtRepository.findBySlug(ACCRINGTON_COURT_SLUG);
        assertThat(courtOptional).isPresent();
        Court court = courtOptional.get();
        final long currentUpdateTime = court.getUpdatedAt().getTime();

        List<CourtEmail> courtEmails = new ArrayList<>();
        final Email email = new Email(TEST_EMAIL, "", "", null);
        courtEmails.add(new CourtEmail(court, email, 0));
        courtEmailRepository.saveAll(courtEmails);

        // Check the timestamp has been updated
        courtOptional = courtRepository.findBySlug(ACCRINGTON_COURT_SLUG);
        assertThat(courtOptional).isPresent();
        assertThat(courtOptional.get().getUpdatedAt().getTime()).isGreaterThan(currentUpdateTime);
    }
}
