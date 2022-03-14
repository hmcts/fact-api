package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate;


public interface CourtApplicationUpdateRepository extends JpaRepository<CourtApplicationUpdate, Integer> {
}
