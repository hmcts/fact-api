package uk.gov.hmcts.dts.fact.services.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtHistory;
import uk.gov.hmcts.dts.fact.repositories.CourtHistoryRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Service for admin court history.
 */
@Service
@Slf4j
public class AdminCourtHistoryService {

    private final CourtHistoryRepository courtHistoryRepository;

    @Autowired
    public AdminCourtHistoryService(CourtHistoryRepository courtHistoryRepository) {
        this.courtHistoryRepository = courtHistoryRepository;
    }

    public List<CourtHistory> getAllCourtHistory() {
        return courtHistoryRepository.findAll()
            .stream()
            .map(CourtHistory::new)
            .toList();
    }

    public CourtHistory getCourtHistoryById(Integer courtHistoryId) {
        return courtHistoryRepository.findById(courtHistoryId)
            .map(CourtHistory::new)
            .orElseThrow(() -> new NotFoundException("Court History with ID: " + courtHistoryId));
    }

    public List<CourtHistory> getCourtHistoryByCourtId(Integer courtId) {
        return courtHistoryRepository.findAllBySearchCourtId(courtId)
            .stream()
            .map(CourtHistory::new)
            .toList();
    }

    public List<CourtHistory> getCourtHistoryByCourtName(String courtName) {
        return courtHistoryRepository.findAllByCourtName(courtName)
            .stream()
            .map(CourtHistory::new)
            .toList();
    }

    public CourtHistory addCourtHistory(CourtHistory courtHistory) {
        return new CourtHistory(
            courtHistoryRepository.save(new uk.gov.hmcts.dts.fact.entity.CourtHistory(courtHistory))
        );
    }

    public CourtHistory updateCourtHistory(CourtHistory courtHistory) {
        uk.gov.hmcts.dts.fact.entity.CourtHistory courtHistoryEntity =
            courtHistoryRepository.findById(courtHistory.getId()).orElseThrow(
                () -> new NotFoundException("Court History with ID: " + courtHistory.getId()));

        // Only properties that can/should be updated.
        courtHistoryEntity.setSearchCourtId(courtHistory.getSearchCourtId());
        courtHistoryEntity.setCourtName(courtHistory.getCourtName());
        courtHistoryEntity.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        return new CourtHistory(courtHistoryRepository.save(courtHistoryEntity));
    }

    @Transactional
    public CourtHistory deleteCourtHistoryById(Integer courtHistoryId) {
        CourtHistory courtHistoryToDelete = getCourtHistoryById(courtHistoryId);
        courtHistoryRepository.deleteById(courtHistoryToDelete.getId());
        return courtHistoryToDelete;
    }

    @Transactional
    public List<CourtHistory> deleteCourtHistoriesByCourtId(Integer courtId) {
        return courtHistoryRepository.deleteCourtHistoriesBySearchCourtId(courtId)
            .stream()
            .map(CourtHistory::new)
            .toList();
    }
}
