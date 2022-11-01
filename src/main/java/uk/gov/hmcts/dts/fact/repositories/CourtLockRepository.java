package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.CourtLock;

import java.util.List;

public interface CourtLockRepository extends JpaRepository<CourtLock, Integer> {
    List<CourtLock> findCourtLockByCourtSlug(String courtSlug);

    List<CourtLock> findCourtLockByCourtSlugAndUserEmail(String courtSlug,
                                                         String userEmail);
}
