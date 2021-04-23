package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.OpeningType;

public interface OpeningTypeRepository extends JpaRepository<OpeningType, Integer> {
}
