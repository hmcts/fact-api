package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;

import java.util.List;

public interface AreasOfLawRepository extends JpaRepository<AreaOfLaw, Integer> {
    List<AreaOfLaw> getAreaOfLawByIdIn(Integer[] ids);
}
