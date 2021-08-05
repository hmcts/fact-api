package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.CourtAdditionalLink;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class CourtAdditionalLinkRepositoryTest {
    @Autowired
    private CourtAdditionalLinkRepository courtAdditionalLinkRepository;

    @Test
    void shouldRetrieveAndUpdateCourtAdditionalLinks() {
        final List<CourtAdditionalLink> results = courtAdditionalLinkRepository.findAll();
        final int additionalLinkSize = results.size();
        assertThat(results).hasSizeGreaterThan(1);

        final List<CourtAdditionalLink> newCourtAdditionalLinks = new ArrayList<>(results);
        final CourtAdditionalLink firstCourtAdditionalLink = results.get(0);
        final CourtAdditionalLink courtAdditionalLinkToAdd = new CourtAdditionalLink(firstCourtAdditionalLink.getCourt(), firstCourtAdditionalLink.getAdditionalLink(), 0);
        newCourtAdditionalLinks.add(courtAdditionalLinkToAdd);

        final List<CourtAdditionalLink> updatedResults = courtAdditionalLinkRepository.saveAll(newCourtAdditionalLinks);
        assertThat(updatedResults).hasSize(additionalLinkSize + 1);

        courtAdditionalLinkRepository.delete(courtAdditionalLinkToAdd);
        final List<CourtAdditionalLink> finalResults = courtAdditionalLinkRepository.findAll();
        assertThat(finalResults).hasSize(additionalLinkSize);
    }
}
