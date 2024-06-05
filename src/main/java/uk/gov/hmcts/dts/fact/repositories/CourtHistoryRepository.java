package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.hmcts.dts.fact.entity.CourtHistory;

import java.util.List;

public interface CourtHistoryRepository extends JpaRepository<CourtHistory, Integer> {

    List<CourtHistory> findAllBySearchCourtId(Integer searchCourtId);

    List<CourtHistory> findAllByCourtName(String courtName);

    List<CourtHistory> findAllByCourtNameIgnoreCase(String courtName);

    List<CourtHistory> deleteCourtHistoriesBySearchCourtId(Integer searchCourtId);
}

