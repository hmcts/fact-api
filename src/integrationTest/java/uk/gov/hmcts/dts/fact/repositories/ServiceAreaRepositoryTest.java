package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.model.ServiceAreaCourt;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class ServiceAreaRepositoryTest {

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Test
    void shouldFindExistingServiceArea() {

        final Optional<uk.gov.hmcts.dts.fact.entity.ServiceArea> result = serviceAreaRepository
            .findBySlugIgnoreCase("money-claims");

        assertThat(result).isPresent();
        final ServiceArea serviceArea = new ServiceArea(result.get());
        assertThat(serviceArea.getServiceAreaCourts().size()).isEqualTo(2);
        final List<ServiceAreaCourt> catchmentTypes = serviceArea.getServiceAreaCourts();
        assertThat(catchmentTypes.get(0).getCatchmentType()).isEqualTo("local");
        assertThat(catchmentTypes.get(0).getSlug()).isEqualTo("reading-county-court-and-family-court");
        assertThat(catchmentTypes.get(0).getCourtName()).isEqualTo("Reading County Court and Family Court");
        assertThat(catchmentTypes.get(1).getCatchmentType()).isEqualTo("national");
        assertThat(catchmentTypes.get(1).getSlug()).isEqualTo("county-court-money-claims-centre-ccmcc");
        assertThat(catchmentTypes.get(1).getCourtName()).isEqualTo("County Court Money Claims Centre (CCMCC)");
    }

    @Test
    void shouldNotFindNonExistentService() {
        final Optional<uk.gov.hmcts.dts.fact.entity.ServiceArea> result = serviceAreaRepository.findBySlugIgnoreCase(
            "nonExistent");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleEmptyString() {
        final Optional<uk.gov.hmcts.dts.fact.entity.ServiceArea> result = serviceAreaRepository.findBySlugIgnoreCase("");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleNull() {
        final Optional<uk.gov.hmcts.dts.fact.entity.ServiceArea> result = serviceAreaRepository.findBySlugIgnoreCase(
            null);
        assertThat(result).isEmpty();
    }
}
