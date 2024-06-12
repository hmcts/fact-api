package uk.gov.hmcts.dts.fact.services.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtHistory;
import uk.gov.hmcts.dts.fact.repositories.CourtHistoryRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

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
    private final CourtRepository courtRepository;

    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminCourtHistoryService(CourtHistoryRepository courtHistoryRepository, CourtRepository courtRepository, AdminAuditService adminAuditService) {
        this.courtHistoryRepository = courtHistoryRepository;
        this.courtRepository = courtRepository;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Get all court histories.
     *
     * @return {@link List} of {@link CourtHistory} list of all court histories
     */
    public List<CourtHistory> getAllCourtHistory() {
        return courtHistoryRepository.findAll()
            .stream()
            .map(CourtHistory::new)
            .toList();
    }

    /**
     * Get a specific court history using ID.
     *
     * @param courtHistoryId ID of a court history
     * @return CourtHistory a specific court history
     */
    public CourtHistory getCourtHistoryById(Integer courtHistoryId) {
        return courtHistoryRepository.findById(courtHistoryId)
            .map(CourtHistory::new)
            .orElseThrow(() -> new NotFoundException("Court History with ID: " + courtHistoryId));
    }

    /**
     * Get all the court histories of a specific court using a search court ID.
     *
     * @param courtId ID of a court
     * @return {@link List} of {@link CourtHistory} list of the court histories of a specific court
     */
    public List<CourtHistory> getCourtHistoryByCourtId(Integer courtId) {
        return courtHistoryRepository.findAllBySearchCourtId(courtId)
            .stream()
            .map(CourtHistory::new)
            .toList();
    }

    /**
     * Get all the court histories matching the court name.
     *
     * @param courtName an old court name
     * @return {@link List} of {@link CourtHistory} list of court histories with matching name
     */
    public List<CourtHistory> getCourtHistoryByCourtName(String courtName) {
        return courtHistoryRepository.findAllByCourtName(courtName)
            .stream()
            .map(CourtHistory::new)
            .toList();
    }

    /**
     * Add a court history.
     * Also saves audit info of the court history creation.
     * @param courtHistory old court info
     * @return CourtHistory the new court history that has been added
     */
    public CourtHistory addCourtHistory(CourtHistory courtHistory) {

        CourtHistory courtHistoryModel = new CourtHistory(courtHistoryRepository.save(new uk.gov.hmcts.dts.fact.entity.CourtHistory(courtHistory)));
        adminAuditService.saveAudit("Create court history", courtHistoryModel, null, courtHistoryModel.getCourtName());
        return courtHistoryModel;
    }

    /**
     * Update an existing court history.
     * Also saves audit info of the court history that has been updated.
     * @param courtHistory updated court info of an existing court history
     * @return CourtHistory the court history that has been updated
     */
    public CourtHistory updateCourtHistory(CourtHistory courtHistory) {
        uk.gov.hmcts.dts.fact.entity.CourtHistory courtHistoryEntity =
            courtHistoryRepository.findById(courtHistory.getId()).orElseThrow(
                () -> new NotFoundException("Court History with ID: " + courtHistory.getId()));

        // Only properties that can/should be updated.
        courtHistoryEntity.setCourtName(courtHistory.getCourtName());
        courtHistoryEntity.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        CourtHistory courtHistoryModel = new CourtHistory(courtHistoryRepository.save(courtHistoryEntity));

        adminAuditService.saveAudit("Update court history", courtHistoryModel, null, courtHistoryModel.getCourtName());
        return courtHistoryModel;
    }

    /**
     * Delete a court history.
     * Also saves audit info of the court history that has been deleted.
     * @param courtHistoryId ID of court history to be deleted
     * @return CourtHistory the court history that has been deleted
     */
    @Transactional
    public CourtHistory deleteCourtHistoryById(Integer courtHistoryId) {
        CourtHistory courtHistoryToDelete = getCourtHistoryById(courtHistoryId);
        courtHistoryRepository.deleteById(courtHistoryToDelete.getId());
        adminAuditService.saveAudit("Delete court history", courtHistoryToDelete, null, courtHistoryToDelete.getCourtName());
        return courtHistoryToDelete;
    }

    /**
     * Delete the court histories of a specific court.
     * Also saves audit info of the court histories that have been deleted.
     * @param courtId ID of court whose histories should be deleted
     * @return CourtHistory the court history that has been deleted
     */
    @Transactional
    public List<CourtHistory> deleteCourtHistoriesByCourtId(Integer courtId) {
        Court court = courtRepository.findCourtById(courtId).orElseThrow(() -> new NotFoundException("Court not found: " + courtId));;
        List<CourtHistory> courtHistoryList = courtHistoryRepository.deleteCourtHistoriesBySearchCourtId(courtId)
            .stream()
            .map(CourtHistory::new)
            .toList();

        adminAuditService.saveAudit("Delete court history", courtHistoryList,
                                    null, court.getSlug());

        return courtHistoryList;
    }

    /**
     * Update court histories of a specific court by slug.
     * Deletes the existing court histories and replaces them with the given court histories
     * @param slug court slug
     * @param courtHistories court histories to replace current ones with
     * @return the courts updated court histories
     */
    @Transactional
    public List<CourtHistory> updateCourtHistoriesBySlug(final String slug, final List<CourtHistory> courtHistories) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException("Court cannot be found. Slug: " + slug));

        List<uk.gov.hmcts.dts.fact.entity.CourtHistory> inputCourtHistories =
            courtHistories.stream().map(uk.gov.hmcts.dts.fact.entity.CourtHistory::new)
                .toList();

        inputCourtHistories.forEach(courtHistory -> courtHistory.setSearchCourtId(courtEntity.getId()));

        List<uk.gov.hmcts.dts.fact.entity.CourtHistory> beforeUpdateCourtHistories = courtHistoryRepository.findAllBySearchCourtId(courtEntity.getId());
        //delete court's existing histories
        courtHistoryRepository.deleteCourtHistoriesBySearchCourtId(courtEntity.getId());

        List<CourtHistory> newCourtHistoryList = courtHistoryRepository.saveAll(inputCourtHistories).stream()
            .map(CourtHistory::new)
            .toList();

        adminAuditService.saveAudit("Update court history", beforeUpdateCourtHistories, newCourtHistoryList, courtEntity.getName());
        //save court's new court histories
        return newCourtHistoryList;
    }

    @Transactional
    public List<CourtHistory> getCourtHistoryByCourtSlug(final String slug) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException("Court cannot be found. Slug: " + slug));

        return courtHistoryRepository.findAllBySearchCourtId(courtEntity.getId()).stream()
            .map(CourtHistory::new)
            .toList();
    }
}
