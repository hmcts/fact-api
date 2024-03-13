package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLaw;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawSpoeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Service for admin court areas of law data.
 */
@Service
public class AdminCourtAreasOfLawService {

    private final CourtRepository courtRepository;
    private final CourtAreaOfLawRepository courtAreaOfLawRepository;
    private final AdminAuditService adminAuditService;
    private final CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository;

    /**
     * Constructor for the AdminCourtAreasOfLawService.
     * @param courtRepository The repository for court
     * @param courtAreaOfLawRepository The repository for court area of law
     * @param courtAreaOfLawSpoeRepository The repository for court area of law spoe
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminCourtAreasOfLawService(final CourtRepository courtRepository, final CourtAreaOfLawRepository courtAreaOfLawRepository,
                                       final CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository, final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.courtAreaOfLawRepository = courtAreaOfLawRepository;
        this.courtAreaOfLawSpoeRepository = courtAreaOfLawSpoeRepository;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Get all court areas of law by slug.
     * @param slug The slug
     * @return A list of areas of law
     */
    public List<AreaOfLaw> getCourtAreasOfLawBySlug(final String slug) {

        Court court = courtRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException(slug));
        return courtAreaOfLawRepository.getCourtAreaOfLawByCourtId(court.getId())
            .stream()
            .map(aol -> new AreaOfLaw(aol.getAreaOfLaw(),
                                      courtAreaOfLawSpoeRepository.getAllByCourtIdAndAreaOfLawId(court.getId(), aol.getAreaOfLaw().getId())
                                          .stream()
                                          .findAny()
                                          .isPresent()))
            .collect(toList());
    }

    /**
     * Update court areas of law.
     * @param slug The slug
     * @param areasOfLaw The areas of law
     * @return A list of areas of law
     */
    @Transactional()
    public List<AreaOfLaw> updateAreasOfLawForCourt(final String slug, final List<AreaOfLaw> areasOfLaw) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        final List<AreaOfLaw> originalCourtAol = getCourtAreasOfLawBySlug(slug);

        List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> newAreasOfLawList = getNewAreasOfLaw(areasOfLaw);
        List<CourtAreaOfLaw> newCourtAreasOfLawList = getNewCourtAreasOfLaw(courtEntity, newAreasOfLawList);

        courtAreaOfLawRepository.deleteCourtAreaOfLawByCourtId(courtEntity.getId());

        List<AreaOfLaw> newAreaOfLawList = courtAreaOfLawRepository
            .saveAll(newCourtAreasOfLawList)
            .stream()
            .map(aol -> new AreaOfLaw(aol.getAreaOfLaw(),
                                      courtAreaOfLawSpoeRepository.getAllByCourtIdAndAreaOfLawId(courtEntity.getId(), aol.getAreaOfLaw().getId())
                                          .stream()
                                          .findAny()
                                          .isPresent()))
            .collect(toList());

        adminAuditService.saveAudit(AuditType.findByName("Update court areas of law"),
                                    originalCourtAol,
                                    newAreaOfLawList,
                                    slug);
        return newAreaOfLawList;
    }

    /**
     * Construct new areas of law.
     * @param areasOfLaw The areas of law
     * @return A list of areas of law
     */
    private List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> getNewAreasOfLaw(final List<AreaOfLaw> areasOfLaw) {
        return areasOfLaw.stream()
            .map(e -> new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(e.getId(), e.getName())).collect(toList());
    }

    /**
     * Construct new court areas of law.
     * @param court The court
     * @param areasOfLaw The areas of law
     * @return A list of court areas of law
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtAreaOfLaw> getNewCourtAreasOfLaw(final Court court,
                                                       final List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> areasOfLaw) {
        final List<CourtAreaOfLaw> newCourtAreasOfLawList = new ArrayList<>();
        for (uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw : areasOfLaw) {
            newCourtAreasOfLawList.add(new CourtAreaOfLaw(areaOfLaw, court));
        }
        return newCourtAreasOfLawList;
    }
}






