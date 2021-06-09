package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtLocalAuthority;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.repositories.CourtLocalAuthorityRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.LocalAuthorityRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtLocalAuthoritiesService {

    private final LocalAuthorityRepository localAuthorityRepository;
    private final CourtRepository courtRepository;
    private final CourtLocalAuthorityRepository courtLocalAuthorityRepository;


    @Autowired
    public AdminCourtLocalAuthoritiesService(final LocalAuthorityRepository localAuthorityRepository, final CourtRepository courtRepository, final CourtLocalAuthorityRepository courtLocalAuthorityRepository) {
        this.localAuthorityRepository = localAuthorityRepository;
        this.courtRepository = courtRepository;
        this.courtLocalAuthorityRepository = courtLocalAuthorityRepository;
    }

    public List<LocalAuthority> getAllLocalAuthorities() {
        return localAuthorityRepository.findAll()
            .stream()
            .map(LocalAuthority::new)
            .collect(toList());
    }

    public List<CourtLocalAuthority> getCourtLocalAuthoritiesBySlug(final String slug) {

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        return courtLocalAuthorityRepository.findByCourtId(courtEntity.getId())
                .stream()
                .map(CourtLocalAuthority::new)
                .collect(toList());

    }

    @Transactional(rollbackFor = {RuntimeException.class})
    public List<CourtLocalAuthority> updateCourtLocalAuthority(final String slug, final List<CourtLocalAuthority> localAuthorities) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return saveNewCourtLocalAuthorities(courtEntity, localAuthorities);

    }

    protected List<CourtLocalAuthority> saveNewCourtLocalAuthorities(final Court courtEntity, final List<CourtLocalAuthority> localAuthorities) {

        final List<uk.gov.hmcts.dts.fact.entity.CourtLocalAuthority> courtLocalAuthorityEntities = getNewCourtLocalAuthority(localAuthorities);
        final List<uk.gov.hmcts.dts.fact.entity.CourtLocalAuthority> existingCourtLocalAuthorities = courtLocalAuthorityRepository.findByCourtId(courtEntity.getId());

        //delete existing Court Local Authorities
        courtLocalAuthorityRepository.deleteAll(existingCourtLocalAuthorities);

        return courtLocalAuthorityRepository
            .saveAll(courtLocalAuthorityEntities)
            .stream()
            .map(CourtLocalAuthority::new)
            .collect(toList());

    }

    private List<uk.gov.hmcts.dts.fact.entity.CourtLocalAuthority> getNewCourtLocalAuthority(final List<CourtLocalAuthority> localAuthorities) {
        return localAuthorities.stream()
            .map(o -> new uk.gov.hmcts.dts.fact.entity.CourtLocalAuthority())
            .collect(toList());
    }


}
