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
    void shouldFindExistingService() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        Service expected = mapper.readValue(
            Paths.get("src/integrationTest/resources/service.json").toFile(),
            Service.class
        );

        Optional<uk.gov.hmcts.dts.fact.entity.Service> result = serviceRepository.findBySlugIgnoreCase(
            "childcare-and-parenting");
        assertThat(result).isPresent();
        Service service = new Service(result.get());
        assertThat(service.getName()).isEqualTo(expected.getName());
        assertThat(service.getDescription()).isEqualTo(expected.getDescription());
        assertThat(service.getServiceAreas()).isEqualTo(expected.getServiceAreas());
        assertThat(service.getSlug()).isEqualTo(expected.getSlug());
    }

    @Test
    void shouldNotFindNonExistentService() {
        Optional<uk.gov.hmcts.dts.fact.entity.Service> service = serviceRepository.findBySlugIgnoreCase("nonExistent");
        assertThat(service).isEmpty();
    }

    @Test
    void shouldHandleEmptyString() {
        Optional<uk.gov.hmcts.dts.fact.entity.Service> service = serviceRepository.findBySlugIgnoreCase("");
        assertThat(service).isEmpty();
    }

    @Test
    void shouldHandleNull() {
        Optional<uk.gov.hmcts.dts.fact.entity.Service> service = serviceRepository.findBySlugIgnoreCase(null);
        assertThat(service).isEmpty();
    }
}
