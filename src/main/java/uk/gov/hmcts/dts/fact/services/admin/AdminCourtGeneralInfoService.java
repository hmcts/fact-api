package uk.gov.hmcts.dts.fact.services.admin;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneralInfo;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import static java.util.stream.Collectors.toList;
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

        if (!courtEntity.getName().equals(generalInfo.getName())) {
            checkIfCourtNameAlreadyExists(generalInfo.getName());
        }

        final CourtGeneralInfo originalGeneralInfo = new CourtGeneralInfo(courtEntity);
        courtEntity.setAlert(generalInfo.getAlert());
        courtEntity.setAlertCy(generalInfo.getAlertCy());

        if (rolesProvider.getRoles().contains(FACT_SUPER_ADMIN)) {
            courtEntity.setName(generalInfo.getName());
            courtEntity.setSlug(convertNameToSlug(generalInfo.getName()));
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

    private List<CourtGeneralInfo> getAllCourtGeneralInfo() {
        return courtRepository.findAll()
            .stream()
            .map(CourtGeneralInfo::new)
            .collect(toList());
    }

    private void checkIfCourtNameAlreadyExists(final String courtName) {
        if (getAllCourtGeneralInfo().stream().anyMatch(generalInfo -> generalInfo.getName().equalsIgnoreCase(courtName))) {
            throw new DuplicatedListItemException("Court name already exists: " + courtName);
        }
    }

    private String convertNameToSlug(final String courtName) {
        return courtName.toLowerCase().replaceAll("[^a-z0-9 ]", "").replace(" ", "-");
    }
}
