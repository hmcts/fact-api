package uk.gov.hmcts.dts.fact.services.admin.list;

import com.launchdarkly.shaded.com.google.gson.Gson;
import com.launchdarkly.shaded.com.google.gson.JsonObject;
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

@Service
public class AdminLocalAuthorityService {

    private final LocalAuthorityRepository localAuthorityRepository;
    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminLocalAuthorityService(final LocalAuthorityRepository localAuthorityRepository,
                                      final AdminAuditService adminAuditService) {
        this.localAuthorityRepository = localAuthorityRepository;
        this.adminAuditService = adminAuditService;
    }

    public List<LocalAuthority> getAllLocalAuthorities() {
        return localAuthorityRepository.findAll()
            .stream()
            .map(LocalAuthority::new)
            .collect(toList());
    }

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
        final uk.gov.hmcts.dts.fact.entity.LocalAuthority existingEntity = localAuthorityEntity.get();
        existingEntity.setName(name);
        LocalAuthority newLocalAuthority = new LocalAuthority(localAuthorityRepository.save(existingEntity));
        adminAuditService.saveAudit(
            AuditType.findByName("Update local authority"),
            new Gson().toJson(localAuthorityEntity),
            new Gson().toJson(newLocalAuthority));
        return newLocalAuthority;
    }

    private void checkIfLocalAuthorityAlreadyExists(final Integer localAuthorityId, final String localAuthorityName) {
        List<uk.gov.hmcts.dts.fact.entity.LocalAuthority> existingLocalAuthorities = localAuthorityRepository.findByName(localAuthorityName);

        if (!existingLocalAuthorities.isEmpty()
            && existingLocalAuthorities.stream().anyMatch(la -> !la.getId().equals(localAuthorityId))) {
            throw new DuplicatedListItemException("Local Authority already exists: " + localAuthorityName);
        }
    }
}
