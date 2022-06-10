package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.repositories.CourtApplicationUpdateRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtApplicationUpdateService {

    private final CourtRepository courtRepository;
    private final CourtApplicationUpdateRepository applicationUpdateRepository;
    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminCourtApplicationUpdateService(final CourtRepository courtRepository,
                                              final CourtApplicationUpdateRepository applicationUpdateRepository,
                                              final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.applicationUpdateRepository = applicationUpdateRepository;
        this.adminAuditService = adminAuditService;
    }

    public List<ApplicationUpdate> getApplicationUpdatesBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtApplicationUpdates()
                .stream()
                .map(CourtApplicationUpdate::getApplicationUpdate)
                .map(ApplicationUpdate::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional()
    public List<ApplicationUpdate> updateApplicationUpdates(final String slug,
                                                            final List<ApplicationUpdate> applicationUpdateList) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        List<uk.gov.hmcts.dts.fact.entity.ApplicationUpdate> newApplicationUpdateList =
            getNewApplicationUpdates(applicationUpdateList);
        List<CourtApplicationUpdate> newCourtApplicationUpdateList =
            getNewCourtApplicationUpdates(courtEntity, newApplicationUpdateList);

        // Remove existing application progression methods and then replace with newly updated ones
        applicationUpdateRepository.deleteAll(courtEntity.getCourtApplicationUpdates());

        List<ApplicationUpdate> resultApplicationUpdateList = applicationUpdateRepository
            .saveAll(newCourtApplicationUpdateList)
            .stream()
            .map(CourtApplicationUpdate::getApplicationUpdate)
            .map(ApplicationUpdate::new)
            .collect(toList());
        adminAuditService.saveAudit(
            AuditType.findByName("Update court application updates list"),
            courtEntity.getCourtApplicationUpdates().stream()
                .map(CourtApplicationUpdate::getApplicationUpdate)
                .map(ApplicationUpdate::new)
                .collect(toList()),
            resultApplicationUpdateList, slug);
        return resultApplicationUpdateList;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private List<uk.gov.hmcts.dts.fact.entity.ApplicationUpdate> getNewApplicationUpdates(
        final List<ApplicationUpdate> applicationUpdateList) {
        return applicationUpdateList.stream()
            .map(e -> new uk.gov.hmcts.dts.fact.entity.ApplicationUpdate(e.getType(), e.getTypeCy(), e.getEmail(), e.getExternalLink(),
                                                                         e.getExternalLinkDescription(), e.getExternalLinkDescriptionCy()))
            .collect(toList());
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtApplicationUpdate> getNewCourtApplicationUpdates(
        final Court court, final List<uk.gov.hmcts.dts.fact.entity.ApplicationUpdate> applicationUpdates) {
        final List<CourtApplicationUpdate> newCourtApplicationUpdatesList = new ArrayList<>();
        for (int i = 0; i < applicationUpdates.size(); i++) {
            newCourtApplicationUpdatesList.add(new CourtApplicationUpdate(court, applicationUpdates.get(i), i));
        }
        return newCourtApplicationUpdatesList;
    }

}
