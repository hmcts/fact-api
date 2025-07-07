package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.entity.ServiceCentre;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.html.sanitizer.OwaspHtmlSanitizer;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;
import uk.gov.hmcts.dts.fact.util.RepoUtils;
import uk.gov.hmcts.dts.fact.util.Utils;

import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

/**
 * Service for admin court general info data.
 */
@Service
public class AdminCourtGeneralInfoService {
    private final CourtRepository courtRepository;
    private final RolesProvider rolesProvider;
    private final AdminAuditService adminAuditService;

    /**
     * Constructor for the AdminCourtGeneralInfoService.
     * @param courtRepository The repository for court
     * @param rolesProvider The service for roles provider
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminCourtGeneralInfoService(final CourtRepository courtRepository, final RolesProvider rolesProvider,
                                        final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.rolesProvider = rolesProvider;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Get court general info by slug.
     * @param slug The slug
     * @return The court general info
     */
    public CourtGeneralInfo getCourtGeneralInfoBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(CourtGeneralInfo::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Update court general info.
     * @param slug The slug
     * @param generalInfo The general info
     * @return The updated court general info
     */
    @Transactional()
    public CourtGeneralInfo updateCourtGeneralInfo(final String slug, final CourtGeneralInfo generalInfo) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        final CourtGeneralInfo originalGeneralInfo = new CourtGeneralInfo(courtEntity);
        courtEntity.setAlert(generalInfo.getAlert());
        courtEntity.setAlertCy(generalInfo.getAlertCy());

        if (rolesProvider.getRoles().contains(FACT_SUPER_ADMIN)) {
            String newSlug = Utils.convertNameToSlug(generalInfo.getName());
            checkIfUpdatedCourtIsValid(courtEntity.getSlug(), newSlug);
            courtEntity.setName(generalInfo.getName());
            courtEntity.setSlug(newSlug);
            courtEntity.setInfo(generalInfo.getInfo());
            courtEntity.setInfoCy(generalInfo.getInfoCy());
            courtEntity.setDisplayed(generalInfo.getOpen());

            if (generalInfo.isServiceCentre()) {
                if (courtEntity.getServiceCentre() == null) {
                    // Update the general info section to include the new row for the service centres
                    // intro paragraph, or otherwise if one exists, alter the rows instead
                    ServiceCentre serviceCentre = new ServiceCentre();
                    serviceCentre.setCourtId(courtEntity);
                    serviceCentre.setIntroParagraph(generalInfo.getScIntroParagraph());
                    serviceCentre.setIntroParagraphCy(generalInfo.getScIntroParagraphCy());
                    courtEntity.setServiceCentre(serviceCentre);
                } else {
                    courtEntity.getServiceCentre().setIntroParagraph(generalInfo.getScIntroParagraph());
                    courtEntity.getServiceCentre().setIntroParagraphCy(generalInfo.getScIntroParagraphCy());
                }
            }
        }

        if (courtEntity.getInPerson() == null) {
            // Cater for new Scenario post covid, where courts can be both in person
            // and not in person, yet still be classed as not being a service centre
            InPerson inPerson = new InPerson();
            inPerson.setIsInPerson(true);
            inPerson.setCourtId(courtEntity);
            inPerson.setAccessScheme(generalInfo.getAccessScheme());
            inPerson.setCommonPlatform(generalInfo.isCommonPlatform());
            courtEntity.setInPerson(inPerson);
        } else {
            courtEntity.getInPerson().setAccessScheme(generalInfo.getAccessScheme());
            courtEntity.getInPerson().setCommonPlatform(generalInfo.isCommonPlatform());
        }

        CourtGeneralInfo updatedGeneralInfo = new CourtGeneralInfo(courtRepository.save(courtEntity));
        adminAuditService.saveAudit(
            AuditType.findByName("Update court general info"),
            originalGeneralInfo,
            updatedGeneralInfo, slug);
        return updatedGeneralInfo;
    }

    /**
     * Compare the base court slug to that of the new slug and if they don't match (i.e. the slug has been updated) then
     * check if the new slug exists in the database. If the slug exists an exception is thrown.
     * @param originalSlug base court slug
     * @param newSlug to be compared to originalSlug
     */
    public void checkIfUpdatedCourtIsValid(final String originalSlug, final String newSlug) {
        if (!originalSlug.equals(newSlug)) {
            RepoUtils.checkIfCourtAlreadyExists(courtRepository, newSlug);
        }
    }
}
