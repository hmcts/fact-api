package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

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
        final CourtGeneralInfo originalGeneralInfo = new CourtGeneralInfo(courtEntity);
        courtEntity.setAlert(generalInfo.getAlert());
        courtEntity.setAlertCy(generalInfo.getAlertCy());

        if (rolesProvider.getRoles().contains(FACT_SUPER_ADMIN)) {
            courtEntity.setInfo(generalInfo.getInfo());
            courtEntity.setInfoCy(generalInfo.getInfoCy());
            courtEntity.setDisplayed(generalInfo.getOpen());
            if (courtEntity.getInPerson() != null && courtEntity.getInPerson().getIsInPerson()) {
                courtEntity.getInPerson().setAccessScheme(generalInfo.getAccessScheme());
            }
        }
        CourtGeneralInfo updatedGeneralInfo = new CourtGeneralInfo(courtRepository.save(courtEntity));
        adminAuditService.saveAudit(
            AuditType.findByName("Update court general info"),
            originalGeneralInfo,
            updatedGeneralInfo, slug);
        return updatedGeneralInfo;
    }
}
