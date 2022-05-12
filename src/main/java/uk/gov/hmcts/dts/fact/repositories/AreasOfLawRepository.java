package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;

import java.util.List;
import java.util.Optional;

public interface AreasOfLawRepository extends JpaRepository<AreaOfLaw, Integer> {
    Optional<List<AreaOfLaw>> findAllByNameIn(List<String> names);
}
