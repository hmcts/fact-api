package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Integer> {
}

