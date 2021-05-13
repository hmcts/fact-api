package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtContact;

public interface CourtContactRepository extends JpaRepository<CourtContact, Integer> {
}
