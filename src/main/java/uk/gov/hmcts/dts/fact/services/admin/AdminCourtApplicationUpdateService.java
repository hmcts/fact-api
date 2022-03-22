package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.model.admin.Contact;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.repositories.CourtApplicationUpdateRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class AdminCourtApplicationUpdateService {

    private final CourtRepository courtRepository;
    private final CourtApplicationUpdateRepository applicationUpdateRepository;
    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminCourtApplicationUpdateService(final CourtRepository courtRepository,
                                              final CourtApplicationUpdateRepository applicationUpdateRepository,
                                              final AdminAuditService adminAuditService){
        this.courtRepository = courtRepository;
        this.applicationUpdateRepository = applicationUpdateRepository;
        this.adminAuditService = adminAuditService;
    }

    public List<ApplicationUpdate> getApplicationUpdatesBySlug(final String slug) {
        System.out.println("GET works");
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtApplicationUpdates()
                .stream()
                .map(CourtApplicationUpdate::getApplicationUpdate)
                .map(ApplicationUpdate::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional()
    public List<ApplicationUpdate> updateApplicationUpdates(final String slug, final List<ApplicationUpdate> applicationUpdateList) {
        System.out.println("Inside PUT");
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        List<uk.gov.hmcts.dts.fact.entity.ApplicationUpdate> newApplicationUpdateList = getNewApplicationUpdates(applicationUpdateList);
        List<CourtApplicationUpdate> newCourtApplicationUpdateList = getNewCourtApplicationUpdates(courtEntity, newApplicationUpdateList);

        // Remove existing application progression methods and then replace with newly updated ones
        applicationUpdateRepository.deleteAll(courtEntity.getCourtApplicationUpdates());

        List<ApplicationUpdate> resultApplicationUpdateList = applicationUpdateRepository
            .saveAll(newCourtApplicationUpdateList)
            .stream()
            .map(CourtApplicationUpdate::getApplicationUpdate)
            .map(ApplicationUpdate::new)
            .collect(toList());
        /*
        adminAuditService.saveAudit(
            AuditType.findByName("Update court application updates list"),
            courtEntity.getCourtApplicationUpdates().stream()
                .map(CourtApplicationUpdate::getApplicationUpdate)
                .map(ApplicationUpdate::new)
                .collect(toList()),
            resultApplicationUpdateList, slug);

         */
        return resultApplicationUpdateList;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private List<uk.gov.hmcts.dts.fact.entity.ApplicationUpdate> getNewApplicationUpdates(final List<ApplicationUpdate> applicationUpdateList) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate> courtApplicationUpdateMap = getCourtApplicationUpdateTypeMap();
        return applicationUpdateList.stream()
            .map(e -> new uk.gov.hmcts.dts.fact.entity.ApplicationUpdate(e.getType(), e.getEmail(), e.getExternalLink(),
                                                                         e.getExternalLinkDescription()

                                                                         ))

            .collect(toList());
    }

    private Map<Integer, uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate> getCourtApplicationUpdateTypeMap() {
        return applicationUpdateRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate::getId, type -> type));
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtApplicationUpdate> getNewCourtApplicationUpdates(final Court court, final List<uk.gov.hmcts.dts.fact.entity.ApplicationUpdate> applicationUpdates) {
        final List<CourtApplicationUpdate> newCourtApplicationUpdatesList = new ArrayList<>();
        for (int i = 0; i < applicationUpdates.size(); i++) {
            newCourtApplicationUpdatesList.add(new CourtApplicationUpdate(court, applicationUpdates.get(i), i));
        }
        return newCourtApplicationUpdatesList;
    }



}






    /*
    @Transactional()
    public List<ApplicationUpdate> updateEmailListForCourt(final String slug, final List<ApplicationUpdate> applicationUpdate) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        List<ApplicationUpdate> originalApplicationUpdateList = getApplicationUpdatesBySlug(slug);
        List<ApplicationUpdate> newApplicationUpdateList = saveNewApplicationUpdates(courtEntity, applicationUpdate);


        adminAuditService.saveAudit(
            AuditType.findByName("Update court contacts"),
            originalContactList,
            newContactList, slug);


        return newApplicationUpdateList;



    }



    private List<ApplicationUpdate> saveNewApplicationUpdates(final Court courtEntity, final List<ApplicationUpdate> applicationUpdates) {
        final List<uk.gov.hmcts.dts.fact.entity.ApplicationUpdate> newApplicationUpdate = getNewApplicationUpdate(applicationUpdates);
        List<CourtApplicationUpdate> newCourtApplicationUpdate = getNewCourtApplicationUpdate(courtEntity, newApplicationUpdate);

        final List<ApplicationUpdate> existingApplicationUpdate = new ArrayList<>(courtEntity.getCourtApplicationUpdates());

        applicationUpdateRepository.deleteAll(existingApplicationUpdate);
        return applicationUpdateRepository.saveAll(newCourtApplicationUpdate)
            .stream()
            .map(CourtApplicationUpdate::getApplicationUpdate)
            .map(ApplicationUpdate::new)
            .collect(toList());
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private List<uk.gov.hmcts.dts.fact.entity.ApplicationUpdate> getNewApplicationUpdate(final List<ApplicationUpdate> applicationUpdates) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate> applicationUpdateTypeMap = getApplicationUpdateTypeMap();
        return applicationUpdates.stream()
            .map(applicationUpdate -> new uk.gov.hmcts.dts.fact.entity.ApplicationUpdate(applicationUpdateTypeMap.get(applicationUpdate.getType()),
                                                                     applicationUpdate.getEmail(),
                                                                     applicationUpdate.getExternalLink(),
                                                                     applicationUpdate.getExternalLinkDescription()))
            .collect(toList());
    }

    private Map<Integer, uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate> getApplicationUpdateTypeMap() {
        return contactTypeRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.ContactType::getId, type -> type));
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtApplicationUpdate> getNewCourtApplicationUpdate(final Court court, final List<uk.gov.hmcts.dts.fact.entity.ApplicationUpdate> applicationUpdates) {
        final List<CourtApplicationUpdate> courtApplicationUpdates = new ArrayList<>();
        for (int i = 0; i < applicationUpdates.size(); i++) {
            applicationUpdates.add(new uk.gov.hmcts.dts.fact.entity.ApplicationUpdate(court, applicationUpdates.get(i), i));
        }
        return courtApplicationUpdates;
    }
}
     */
