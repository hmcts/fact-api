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

    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminCourtHistoryService(CourtHistoryRepository courtHistoryRepository, AdminAuditService adminAuditService) {
        this.courtHistoryRepository = courtHistoryRepository;
        this.adminAuditService = adminAuditService;
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

        CourtHistory courtHistoryModel = new CourtHistory(courtHistoryRepository.save(new uk.gov.hmcts.dts.fact.entity.CourtHistory(courtHistory)));
        adminAuditService.saveAudit("Create court history", courtHistoryModel, null, courtHistoryModel.getCourtName());
        return courtHistoryModel;
    }

    public CourtHistory updateCourtHistory(CourtHistory courtHistory) {
        uk.gov.hmcts.dts.fact.entity.CourtHistory courtHistoryEntity =
            courtHistoryRepository.findById(courtHistory.getId()).orElseThrow(
                () -> new NotFoundException("Court History with ID: " + courtHistory.getId()));

        // Only properties that can/should be updated.
        courtHistoryEntity.setSearchCourtId(courtHistory.getSearchCourtId());
        courtHistoryEntity.setCourtName(courtHistory.getCourtName());
        courtHistoryEntity.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        CourtHistory courtHistoryModel = new CourtHistory(courtHistoryRepository.save(courtHistoryEntity));

        adminAuditService.saveAudit("Update court history", courtHistoryModel, null, courtHistoryModel.getCourtName());
        return courtHistoryModel;
    }

    @Transactional
    public CourtHistory deleteCourtHistoryById(Integer courtHistoryId) {
        CourtHistory courtHistoryToDelete = getCourtHistoryById(courtHistoryId);
        courtHistoryRepository.deleteById(courtHistoryToDelete.getId());
        adminAuditService.saveAudit("Delete court history", courtHistoryToDelete, null, courtHistoryToDelete.getCourtName());
        return courtHistoryToDelete;
    }

    @Transactional
    public List<CourtHistory> deleteCourtHistoriesByCourtId(Integer courtId) {
        List<CourtHistory> courtHistoryList = courtHistoryRepository.deleteCourtHistoriesBySearchCourtId(courtId)
            .stream()
            .map(CourtHistory::new)
            .toList();

        adminAuditService.saveAudit("Delete court history", courtHistoryList,
                                    null, courtHistoryList.toString());

        return courtHistoryList;
    }
}
