package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.County;

public interface CountyRepository extends JpaRepository<County, Integer> {
}
