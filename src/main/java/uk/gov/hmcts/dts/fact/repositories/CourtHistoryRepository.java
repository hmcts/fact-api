package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtHistory;

import java.util.List;

/**
 * Repository for accessing CourtHistory data.
 */
public interface CourtHistoryRepository extends JpaRepository<CourtHistory, Integer> {

    List<CourtHistory> findAllBySearchCourtId(Integer searchCourtId);

    List<CourtHistory> findAllByCourtName(String courtName);

    List<CourtHistory> findAllByCourtNameIgnoreCaseOrderByUpdatedAtDesc(String courtName);

    List<CourtHistory> deleteCourtHistoriesBySearchCourtId(Integer searchCourtId);
}

