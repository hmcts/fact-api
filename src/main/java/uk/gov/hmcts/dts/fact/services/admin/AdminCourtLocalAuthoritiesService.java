package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtLocalAuthorityAreaOfLaw;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.repositories.CourtLocalAuthorityAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Service for admin court local authorities data.
 */
@Service
public class AdminCourtLocalAuthoritiesService {

    private final CourtRepository courtRepository;
    private final CourtLocalAuthorityAreaOfLawRepository courtLocalAuthorityAreaOfLawRepository;
    private final AdminAuditService adminAuditService;

    /**
     * Constructor for the AdminCourtLocalAuthoritiesService.
     * @param courtRepository The repository for court
     * @param courtLocalAuthorityAreaOfLawRepository The repository for court local authority area of law
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminCourtLocalAuthoritiesService(final CourtRepository courtRepository,
                                             final CourtLocalAuthorityAreaOfLawRepository courtLocalAuthorityAreaOfLawRepository,
                                             final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.courtLocalAuthorityAreaOfLawRepository = courtLocalAuthorityAreaOfLawRepository;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Get all court local authorities by slug and area of law.
     * @param slug The slug
     * @param areaOfLaw The area of law
     * @return A list of local authorities
     */
    public List<LocalAuthority> getCourtLocalAuthoritiesBySlugAndAreaOfLaw(final String slug, final String areaOfLaw) {

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        return courtLocalAuthorityAreaOfLawRepository.findByCourtId(courtEntity.getId())
                .stream()
                .filter(c -> c.getAreaOfLaw().getName().equals(areaOfLaw))
                .map(la -> new LocalAuthority(la.getLocalAuthority().getId(), la.getLocalAuthority().getName()))
                .collect(toList());
    }

    /**
     * Update court local authorities.
     * @param slug The slug
     * @param areaOfLaw The area of law
     * @param localAuthorities The local authorities
     * @return A list of local authorities
     */
    @Transactional(rollbackFor = {RuntimeException.class})
    public List<LocalAuthority> updateCourtLocalAuthority(final String slug, final String areaOfLaw, final List<LocalAuthority> localAuthorities) {

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        final AreaOfLaw areaOfLawEntity = courtEntity.getAreasOfLaw()
            .stream()
            .filter(al -> al.getName().equals(areaOfLaw)).findFirst()
            .orElseThrow(() -> new NotFoundException(areaOfLaw));
        final List<CourtLocalAuthorityAreaOfLaw> originalCourtLocalAuthorities = getExistingCourtLocalAuthorities(courtEntity, areaOfLawEntity);

        List<LocalAuthority> updatedLocalAuthorities =
            saveNewCourtLocalAuthorities(courtEntity, areaOfLawEntity, localAuthorities, originalCourtLocalAuthorities);

        adminAuditService.saveAudit(
            AuditType.findByName("Update court local authorities"),
            originalCourtLocalAuthorities
                            .stream()
                            .map(la -> new LocalAuthority(la.getLocalAuthority().getId(), la.getLocalAuthority().getName()))
                            .collect(toList()),
            updatedLocalAuthorities, slug);
        return updatedLocalAuthorities;
    }

    /**
     * Save new court local authorities.
     * @param courtEntity The court entity
     * @param areaOflaw The area of law
     * @param localAuthorities The local authorities
     * @param existingCourtLocalAuthorities The existing court local authorities
     * @return A list of local authorities
     */
    protected List<LocalAuthority> saveNewCourtLocalAuthorities(final Court courtEntity, final AreaOfLaw areaOflaw,
                                                                final List<LocalAuthority> localAuthorities,
                                                                final List<CourtLocalAuthorityAreaOfLaw> existingCourtLocalAuthorities) {

        final List<CourtLocalAuthorityAreaOfLaw> courtLocalAuthorityAreaOfLawEntities = getNewCourtLocalAuthorities(courtEntity, areaOflaw, localAuthorities);

        //delete existing Court Local Authorities for Area of Law
        courtLocalAuthorityAreaOfLawRepository.deleteAll(existingCourtLocalAuthorities);

        return courtLocalAuthorityAreaOfLawRepository
            .saveAll(courtLocalAuthorityAreaOfLawEntities)
            .stream()
            .map(la -> new LocalAuthority(la.getLocalAuthority().getId(), la.getLocalAuthority().getName()))
            .collect(toList());
    }

    /**
     * Get new court local authorities.
     * @param courtEntity The court entity
     * @param areaOflaw The area of law
     * @param localAuthorities The local authorities
     * @return A list of court local authorities
     */
    private List<CourtLocalAuthorityAreaOfLaw> getNewCourtLocalAuthorities(final Court courtEntity, final AreaOfLaw areaOflaw, final List<LocalAuthority> localAuthorities) {
        return localAuthorities.stream()
            .map(o -> new CourtLocalAuthorityAreaOfLaw(areaOflaw, courtEntity, new uk.gov.hmcts.dts.fact.entity.LocalAuthority(o.getId(), o.getName())))
            .collect(toList());
    }

    /**
     * Get existing court local authorities.
     * @param courtEntity The court entity
     * @param areaOflaw The area of law
     * @return A list of court local authorities
     */
    private List<CourtLocalAuthorityAreaOfLaw> getExistingCourtLocalAuthorities(final Court courtEntity, final AreaOfLaw areaOflaw) {
        return courtLocalAuthorityAreaOfLawRepository.findByCourtId(courtEntity.getId())
            .stream()
            .filter(c -> c.getAreaOfLaw().equals(areaOflaw))
            .collect(toList());
    }

}
