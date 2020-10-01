package uk.gov.hmcts.dts.fact.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.Court;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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

        uk.gov.hmcts.dts.fact.model.Court expected = mapper.readValue(
            Paths.get("src/integrationTest/resources/aylesbury-magistrates-court-and-family-court.json").toFile(),
            uk.gov.hmcts.dts.fact.model.Court.class
        );

        Optional<Court> result = courtRepository.findBySlug("aylesbury-magistrates-court-and-family-court");
        assertThat(result).isPresent();
        uk.gov.hmcts.dts.fact.model.Court court = new uk.gov.hmcts.dts.fact.model.Court(result.get());
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
        final String nameRegex = "%Administrative Court%";

        ObjectMapper mapper = new ObjectMapper();

        List<uk.gov.hmcts.dts.fact.model.CourtReference> expected = Arrays.asList(mapper.readValue(
            Paths.get("src/integrationTest/resources/courts.json").toFile(),
            uk.gov.hmcts.dts.fact.model.CourtReference[].class
        ));

        List<Court> result = courtRepository.queryBy(query, nameRegex);
        assertThat(result.size()).isGreaterThanOrEqualTo(1);
        uk.gov.hmcts.dts.fact.model.CourtReference court =
            new uk.gov.hmcts.dts.fact.model.CourtReference(result.get(0).getName(), result.get(0).getSlug());
        assertThat(court.getName()).isEqualTo(expected.get(0).getName());
        assertThat(court.getSlug()).isEqualTo(expected.get(0).getSlug());
    }

    @Test
    void shouldNotFindNonExistentCourtByQuery() {
        List<Court> result = courtRepository.queryBy("This does not exist", "");
        assertThat(result).isEmpty();
    }
}
