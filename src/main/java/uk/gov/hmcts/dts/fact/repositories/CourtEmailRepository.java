package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;

public interface CourtEmailRepository extends JpaRepository<CourtEmail, Integer> {
}
