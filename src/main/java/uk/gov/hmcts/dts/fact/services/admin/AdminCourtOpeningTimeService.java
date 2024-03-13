package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.OpeningTime;
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.OpeningTypeRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Service for admin court opening time data.
 */
@Service
public class AdminCourtOpeningTimeService {
    private final CourtRepository courtRepository;
    private final OpeningTypeRepository openingTypeRepository;
    private final AdminAuditService adminAuditService;

    /**
     * Constructor for the AdminCourtOpeningTimeService.
     * @param courtRepository The repository for court
     * @param openingTypeRepository The repository for opening type
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminCourtOpeningTimeService(final CourtRepository courtRepository,
                                        final OpeningTypeRepository openingTypeRepository,
                                        final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.openingTypeRepository = openingTypeRepository;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Get the opening times for a court by slug.
     * @param slug The slug of the court
     * @return The opening times for the court
     */
    public List<OpeningTime> getCourtOpeningTimesBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtOpeningTimes()
                .stream()
                .map(CourtOpeningTime::getOpeningTime)
                .map(OpeningTime::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Update the opening times for a court by slug.
     * @param slug The slug of the court
     * @param openingTimes The new opening times
     * @return The updated opening times for the court
     */
    @Transactional()
    public List<OpeningTime> updateCourtOpeningTimes(final String slug, final List<OpeningTime> openingTimes) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        List<OpeningTime> originalOpeningTimes = getCourtOpeningTimesBySlug(slug);
        List<OpeningTime> updatedOpeningTimes = saveNewOpeningTimes(courtEntity, openingTimes);
        adminAuditService.saveAudit(
            AuditType.findByName("Update court opening times"),
            originalOpeningTimes,
            updatedOpeningTimes, slug);
        return updatedOpeningTimes;
    }

    /**
     * Get all the opening types for a court.
     * @return The opening types for a court
     */
    public List<OpeningType> getAllCourtOpeningTypes() {
        return openingTypeRepository.findAll()
            .stream()
            .map(OpeningType::new)
            .sorted(Comparator.comparing(OpeningType::getType))
            .collect(toList());
    }

    /**
     * Save the new opening times for a court.
     * @param courtEntity The court entity
     * @param openingTimes The new opening times
     * @return The updated opening times for the court
     */
    private List<OpeningTime> saveNewOpeningTimes(final Court courtEntity, final List<OpeningTime> openingTimes) {
        List<uk.gov.hmcts.dts.fact.entity.OpeningTime> openingTimeEntities = getNewOpeningTimes(openingTimes);
        List<CourtOpeningTime> courtOpeningTimeEntities = getNewCourtOpeningTimes(courtEntity, openingTimeEntities);

        if (courtEntity.getCourtOpeningTimes() == null) {
            courtEntity.setCourtOpeningTimes(courtOpeningTimeEntities);
        } else {
            courtEntity.getCourtOpeningTimes().clear();
            courtEntity.getCourtOpeningTimes().addAll(courtOpeningTimeEntities);
        }
        final Court courtWithUpdatedOpeningTime = courtRepository.save(courtEntity);

        return courtWithUpdatedOpeningTime.getCourtOpeningTimes()
            .stream()
            .map(CourtOpeningTime::getOpeningTime)
            .map(OpeningTime::new)
            .collect(toList());
    }

    /**
     * Get the new opening types.
     * @return The opening type entity
     */
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.DataflowAnomalyAnalysis"})
    private List<uk.gov.hmcts.dts.fact.entity.OpeningTime> getNewOpeningTimes(final List<OpeningTime> openingTimes) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.OpeningType> openingTypeMap = getOpeningTypeMap();
        final List<uk.gov.hmcts.dts.fact.entity.OpeningTime> openingTimeEntities = new ArrayList<>();

        for (OpeningTime openingTime : openingTimes) {
            final uk.gov.hmcts.dts.fact.entity.OpeningType openingType = openingTypeMap.get(openingTime.getTypeId());
            openingTimeEntities.add(new uk.gov.hmcts.dts.fact.entity.OpeningTime(openingType, openingTime.getHours()));
        }
        return openingTimeEntities;
    }

    /**
     * Map for opening types.
     * @return The opening types for the court
     */
    private Map<Integer, uk.gov.hmcts.dts.fact.entity.OpeningType> getOpeningTypeMap() {
        return openingTypeRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.OpeningType::getId, type -> type));
    }

    /**
     * Get the new court opening times.
     * @return The court opening times
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtOpeningTime> getNewCourtOpeningTimes(final Court court, final List<uk.gov.hmcts.dts.fact.entity.OpeningTime> openingTimes) {
        final List<CourtOpeningTime> courtOpeningTimes = new ArrayList<>();
        for (int i = 0; i < openingTimes.size(); i++) {
            courtOpeningTimes.add(new CourtOpeningTime(court, openingTimes.get(i), i));
        }
        return courtOpeningTimes;
    }
}
