package uk.gov.hmcts.dts.fact.services.admin.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.repositories.LocalAuthorityRepository;
import uk.gov.hmcts.dts.fact.services.admin.AdminAuditService;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Service for admin local authority data.
 */
@Service
public class AdminLocalAuthorityService {

    private final LocalAuthorityRepository localAuthorityRepository;
    private final AdminAuditService adminAuditService;

    /**
     * Constructor for the AdminLocalAuthorityService.
     * @param localAuthorityRepository The repository for local authority
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminLocalAuthorityService(final LocalAuthorityRepository localAuthorityRepository,
                                      final AdminAuditService adminAuditService) {
        this.localAuthorityRepository = localAuthorityRepository;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Get all local authorities.
     * @return The local authorities
     */
    public List<LocalAuthority> getAllLocalAuthorities() {
        return localAuthorityRepository.findAll()
            .stream()
            .map(LocalAuthority::new)
            .collect(toList());
    }

    /**
     * Update a local authority by id.
     * @param localAuthorityId The id of the local authority to update
     * @param name The new name of the local authority
     * @return The local authority
     */
    @Transactional
    public LocalAuthority updateLocalAuthority(final Integer localAuthorityId, final String name) {

        // Ensure entity with given ID exists
        final Optional<uk.gov.hmcts.dts.fact.entity.LocalAuthority> localAuthorityEntity = localAuthorityRepository.findById(localAuthorityId);
        if (localAuthorityEntity.isEmpty()) {
            throw new NotFoundException(localAuthorityId.toString());
        }

        // Ensure we are not going to create a duplicate of an existing local authority
        checkIfLocalAuthorityAlreadyExists(localAuthorityId, name);

        // Change local authority entity name
        final List<LocalAuthority> originalList = getAllLocalAuthorities();
        final uk.gov.hmcts.dts.fact.entity.LocalAuthority existingEntity = localAuthorityEntity.get();
        existingEntity.setName(name);
        LocalAuthority newLocalAuthority = new LocalAuthority(localAuthorityRepository.save(existingEntity));
        adminAuditService.saveAudit(
            AuditType.findByName("Update local authority"),
            originalList,
            getAllLocalAuthorities(),
            null);
        return newLocalAuthority;
    }

    /**
     * Check if a local authority already exists.
     * @param localAuthorityId The id of the local authority
     * @param localAuthorityName The name of the local authority
     * @throws DuplicatedListItemException if the local authority already exists
     */
    private void checkIfLocalAuthorityAlreadyExists(final Integer localAuthorityId, final String localAuthorityName) {
        List<uk.gov.hmcts.dts.fact.entity.LocalAuthority> existingLocalAuthorities = localAuthorityRepository.findByName(localAuthorityName);

        if (!existingLocalAuthorities.isEmpty()
            && existingLocalAuthorities.stream().anyMatch(la -> !la.getId().equals(localAuthorityId))) {
            throw new DuplicatedListItemException("Local Authority already exists: " + localAuthorityName);
        }
    }
}
