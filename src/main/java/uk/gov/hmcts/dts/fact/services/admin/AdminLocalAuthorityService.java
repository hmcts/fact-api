package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.repositories.LocalAuthorityRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class AdminLocalAuthorityService {

    private final LocalAuthorityRepository localAuthorityRepository;

    @Autowired
    public AdminLocalAuthorityService(final LocalAuthorityRepository localAuthorityRepository) {
        this.localAuthorityRepository = localAuthorityRepository;
    }

    public List<LocalAuthority> getAllLocalAuthorities() {
        return localAuthorityRepository.findAll()
            .stream()
            .map(LocalAuthority::new)
            .collect(toList());
    }

    @Transactional(rollbackFor = {RuntimeException.class})
    public LocalAuthority updateLocalAuthority(final Integer localAuthorityId, final LocalAuthority localAuthorityDetails) {

        // Ensure entity with given ID exists
        final Optional<uk.gov.hmcts.dts.fact.entity.LocalAuthority> localAuthorityEntity = localAuthorityRepository.findById(localAuthorityId);
        if (localAuthorityEntity.isEmpty()) {
            throw new NotFoundException(localAuthorityId.toString());
        }

        // Change local authority entity name
        final uk.gov.hmcts.dts.fact.entity.LocalAuthority existingEntity = localAuthorityEntity.get();
        existingEntity.setName(localAuthorityDetails.getName());
        return new LocalAuthority(localAuthorityRepository.save(existingEntity));
    }
}
