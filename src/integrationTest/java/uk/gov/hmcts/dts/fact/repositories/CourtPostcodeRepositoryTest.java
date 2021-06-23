package uk.gov.hmcts.dts.fact.repositories;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtPostcode;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourtPostcodeRepositoryTest {
    private static final String TEST_MANCHESTER_COURT_SLUG = "manchester-civil-justice-centre-civil-and-family-courts";
    private static final String TEST_MANCHESTER_POSTCODE = "M11";
    private static final String TEST_BIRMINGHAM_POSTCODE = "B11";
    private static final String TEST_POSTCODE = "Z11AA";

    @Autowired
    private CourtPostcodeRepository courtPostcodeRepository;

    @Autowired
    private CourtRepository courtRepository;

    private Optional<Court> court;
    private int courtId;

    @BeforeAll
    void setUp() {
        court = courtRepository.findBySlug(TEST_MANCHESTER_COURT_SLUG);
        assertThat(court).isPresent();
        courtId = court.get().getId();
    }

    @Test
    void shouldRetrieveCourtPostcodeUsingAValidPostcodeOnly() {
        assertThat(courtPostcodeRepository.findByCourtIdAndPostcode(courtId, TEST_MANCHESTER_POSTCODE)).isNotEmpty();
        assertThat(courtPostcodeRepository.findByCourtIdAndPostcode(courtId, TEST_BIRMINGHAM_POSTCODE)).isEmpty();
    }

    @Test
    void shouldDeleteByCourtIdAndPostcode() {
        // Add a postcode twice and test both instances can be deleted
        courtPostcodeRepository.save(new CourtPostcode(TEST_POSTCODE, court.get()));
        courtPostcodeRepository.save(new CourtPostcode(TEST_POSTCODE, court.get()));

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(courtPostcodeRepository.findByCourtIdAndPostcode(courtId, TEST_POSTCODE)).isNotEmpty();
        softly.assertThat(courtPostcodeRepository.deleteByCourtIdAndPostcode(courtId, TEST_POSTCODE)).hasSize(2);
        softly.assertThat(courtPostcodeRepository.findByCourtIdAndPostcode(courtId, TEST_POSTCODE)).isEmpty();
        softly.assertAll();
    }
}
