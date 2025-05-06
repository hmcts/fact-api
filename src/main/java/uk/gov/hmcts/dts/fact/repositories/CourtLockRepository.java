package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.dts.fact.entity.CourtLock;

import java.util.List;

public interface CourtLockRepository extends JpaRepository<CourtLock, Integer> {

    List<CourtLock> findCourtLockByCourtSlug(String courtSlug);

    List<CourtLock> findCourtLockByCourtSlugAndUserEmail(String courtSlug,
                                                         String userEmail);

    // Method to find the locks before deleting them
    List<CourtLock> findByUserEmail(String userEmail);

    // Method for the direct delete operation
    @Modifying
    @Query("DELETE FROM CourtLock cl WHERE cl.userEmail = :email")
    int deleteDirectByUserEmail(@Param("email") String userEmail);

}
