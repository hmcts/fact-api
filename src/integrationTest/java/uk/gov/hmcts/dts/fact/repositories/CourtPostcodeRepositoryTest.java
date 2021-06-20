package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.Court;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class CourtPostcodeRepositoryTest {
    private static final String TEST_MANCHESTER_COURT_SLUG = "manchester-civil-justice-centre-civil-and-family-courts";
    private static final String TEST_MANCHESTER_POSTCODE = "M11";
    private static final String TEST_BIRMINGHAM_POSTCODE = "B11";

    @Autowired
    private CourtPostcodeRepository courtPostcodeRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Test
    void shouldRetrieveCourtPostcodeUsingAValidPostcodeOnly() {
        final Optional<Court> court = courtRepository.findBySlug(TEST_MANCHESTER_COURT_SLUG);
        assertThat(court).isPresent();
        final int courtId = court.get().getId();
        assertThat(courtPostcodeRepository.findByCourtIdAndPostcode(courtId, TEST_MANCHESTER_POSTCODE)).isPresent();
        assertThat(courtPostcodeRepository.findByCourtIdAndPostcode(courtId, TEST_BIRMINGHAM_POSTCODE)).isNotPresent();
    }
}
