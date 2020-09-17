package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.Court;

public interface CourtRepository extends JpaRepository<Court, Integer> {
    Court findBySlug(String slug);
}
