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
class CourtRepositoryTest {

    protected static final String ABERDEEN_TRIBUNAL_HEARING_CENTRE = "aberdeen-tribunal-hearing-centre";

    @Autowired
    private CourtRepository courtRepository;

    @Test
    void shouldFindExistingCourt() {
        Optional<Court> court = courtRepository.findBySlug(ABERDEEN_TRIBUNAL_HEARING_CENTRE);
        assertThat(court).isNotEmpty();
        assertThat(court.get().getSlug()).isEqualTo(ABERDEEN_TRIBUNAL_HEARING_CENTRE);

    }

    @Test
    void shouldNotFindNonExistantCourt() {
        Optional<Court> court = courtRepository.findBySlug("wibble");
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
}
