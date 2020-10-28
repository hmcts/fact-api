package uk.gov.hmcts.dts.fact.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.model.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class ServiceRepositoryTest {

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    void shouldFindExistingCourt() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        Service expected = mapper.readValue(
            Paths.get("src/integrationTest/resources/service.json").toFile(),
            Service.class
        );

        Optional<uk.gov.hmcts.dts.fact.entity.Service> result = serviceRepository.findByNameIgnoreCase("Money");
        assertThat(result).isPresent();
        Service moneyService = new Service(result.get());
        assertThat(moneyService.getName()).isEqualTo(expected.getName());
        assertThat(moneyService.getDescription()).isEqualTo(expected.getDescription());
        assertThat(moneyService.getServiceAreas()).isEqualTo(expected.getServiceAreas());
    }

    @Test
    void shouldNotFindNonExistentCourt() {
        Optional<uk.gov.hmcts.dts.fact.entity.Service> service = serviceRepository.findByNameIgnoreCase("nonExistent");
        assertThat(service).isEmpty();
    }

    @Test
    void shouldHandleEmptyString() {
        Optional<uk.gov.hmcts.dts.fact.entity.Service> service = serviceRepository.findByNameIgnoreCase("");
        assertThat(service).isEmpty();
    }

    @Test
    void shouldHandleNull() {
        Optional<uk.gov.hmcts.dts.fact.entity.Service> service = serviceRepository.findByNameIgnoreCase(null);
        assertThat(service).isEmpty();
    }

}
