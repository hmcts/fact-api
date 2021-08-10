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
public class CourtAdditionalLinkRepositoryTest {
    @Autowired
    private CourtAdditionalLinkRepository courtAdditionalLinkRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Test
    void shouldDeleteCourtAdditionalLinksByCourtId() {
        final Optional<Court> court = courtRepository.findBySlug("manchester-civil-justice-centre-civil-and-family-courts");
        assertThat(court).isPresent();

        final Integer courtId = court.get().getId();
        assertThat(courtAdditionalLinkRepository.findCourtAdditionalLinksByCourtId(courtId)).hasSizeGreaterThan(1);

        courtAdditionalLinkRepository.deleteCourtAdditionalLinksByCourtId(courtId);
        assertThat(courtAdditionalLinkRepository.findCourtAdditionalLinksByCourtId(courtId)).isEmpty();
    }
}
