package uk.gov.hmcts.dts.fact.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.Court;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CourtRepositoryTest {

    @Autowired
    private CourtRepository courtRepository;

    @Test
    void shouldFindExistingCourt() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        uk.gov.hmcts.dts.fact.model.Court courtModel = mapper.readValue(
            Paths.get("src/integrationTest/resources/aylesbury-magistrates-court-and-family-court.json").toFile(),
            uk.gov.hmcts.dts.fact.model.Court.class
        );

        Optional<Court> result = courtRepository.findBySlug("aylesbury-magistrates-court-and-family-court");
        assertThat(result).isNotEmpty();
        Court court = result.get();
        assertThat(court.getSlug()).isEqualTo(courtModel.getSlug());
        //        assertThat(court.getAreasOfLaw()).isEqualTo(courtModel.getAreasOfLaw());
        //        assertThat(court.getContacts()).isEqualTo(courtModel.getContacts());
        //        assertThat(court.getAddresses()).isEqualTo(courtModel.getAddresses());
        //        assertThat(court.getAddresses().get(0).getAddressType().getName()).isNotEqualTo("");
        //        assertThat(court.getEmails()).isEqualTo(courtModel.getEmails());
        //        assertThat(court.getCourtTypes()).isEqualTo(courtModel.getCourtTypes());
        //        assertThat(court.getFacilities()).isEqualTo(courtModel.getFacilities());
        //        assertThat(court.getOpeningTimes()).isEqualTo(courtModel.getOpeningTimes());
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
}
