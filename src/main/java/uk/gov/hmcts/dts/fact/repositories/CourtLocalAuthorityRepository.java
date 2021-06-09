package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtLocalAuthority;

import java.util.List;


public interface CourtLocalAuthorityRepository extends JpaRepository<CourtLocalAuthority, Integer> {
    List<CourtLocalAuthority> findByCourtId(Integer courtId);
}
