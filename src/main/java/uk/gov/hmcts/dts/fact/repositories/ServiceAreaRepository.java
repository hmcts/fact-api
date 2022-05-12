package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;

import java.util.List;
import java.util.Optional;

public interface ServiceAreaRepository extends JpaRepository<ServiceArea, Integer> {
    Optional<ServiceArea> findBySlugIgnoreCase(String slug);

    List<ServiceArea> findByAreaOfLawId(Integer areaOfLawId);

    Optional<List<ServiceArea>> findAllByNameIn(List<String> serviceAreas);

    ServiceArea findByName(String name);
}
