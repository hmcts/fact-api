package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.InPerson;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.html.sanitizer.OwaspHtmlSanitizer;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;
import uk.gov.hmcts.dts.fact.util.RepoUtils;
import uk.gov.hmcts.dts.fact.util.Utils;

import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_SUPER_ADMIN;

@Service
public class AdminCourtGeneralInfoService {
    private final CourtRepository courtRepository;
    private final RolesProvider rolesProvider;
    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminCourtGeneralInfoService(final CourtRepository courtRepository, final RolesProvider rolesProvider,
                                        final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.rolesProvider = rolesProvider;
        this.adminAuditService = adminAuditService;
    }

    public CourtGeneralInfo getCourtGeneralInfoBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(CourtGeneralInfo::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional()
    public CourtGeneralInfo updateCourtGeneralInfo(final String slug, final CourtGeneralInfo generalInfo) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        System.out.println(generalInfo);

        final CourtGeneralInfo originalGeneralInfo = new CourtGeneralInfo(courtEntity);
        courtEntity.setAlert(OwaspHtmlSanitizer.sanitizeHtml(generalInfo.getAlert()));
        courtEntity.setAlertCy(OwaspHtmlSanitizer.sanitizeHtml(generalInfo.getAlertCy()));

        if (rolesProvider.getRoles().contains(FACT_SUPER_ADMIN)) {
            checkIfUpdatedCourtIsValid(courtEntity.getName(), generalInfo.getName());
            courtEntity.setName(generalInfo.getName());
            courtEntity.setSlug(Utils.convertNameToSlug(generalInfo.getName()));
            courtEntity.setInfo(generalInfo.getInfo());
            courtEntity.setInfoCy(generalInfo.getInfoCy());
            courtEntity.setDisplayed(generalInfo.getOpen());
            if (courtEntity.getInPerson() != null && courtEntity.getInPerson().getIsInPerson()) {
                courtEntity.getInPerson().setAccessScheme(generalInfo.getAccessScheme());
            } else {
                InPerson inPerson = new InPerson();
            }
        }
        CourtGeneralInfo updatedGeneralInfo = new CourtGeneralInfo(courtRepository.save(courtEntity));
        adminAuditService.saveAudit(
            AuditType.findByName("Update court general info"),
            originalGeneralInfo,
            updatedGeneralInfo, slug);
        return updatedGeneralInfo;
    }

    /**
     * Compare the base court name to that of the new name and if they don't match (i.e. the name has been updated) then
     * check if the new name exists in the database. If the name exists an exception is thrown.
     * @param originalCourtName base court name
     * @param newCourtName to be compared to originalCourtName
     */
    public void checkIfUpdatedCourtIsValid(final String originalCourtName, final String newCourtName) {
        if (!originalCourtName.equals(newCourtName)) {
            RepoUtils.checkIfCourtAlreadyExists(courtRepository, Utils.convertNameToSlug(newCourtName));
        }
    }
}
