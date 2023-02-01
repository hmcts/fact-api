package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAdditionalLink;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AdditionalLink;
import uk.gov.hmcts.dts.fact.repositories.CourtAdditionalLinkRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtAdditionalLinkService {
    private final CourtRepository courtRepository;
    private final CourtAdditionalLinkRepository courtAdditionalLinkRepository;
    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminCourtAdditionalLinkService(final CourtRepository courtRepository,
                                           final CourtAdditionalLinkRepository courtAdditionalLinkRepository,
                                           final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.courtAdditionalLinkRepository = courtAdditionalLinkRepository;
        this.adminAuditService = adminAuditService;
    }

    public List<AdditionalLink> getCourtAdditionalLinksBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtAdditionalLinks()
                .stream()
                .map(CourtAdditionalLink::getAdditionalLink)
                .map(AdditionalLink::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional
    public List<AdditionalLink> updateCourtAdditionalLinks(final String slug, final List<AdditionalLink> additionalLinks) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        final List<CourtAdditionalLink> newCourtAdditionalLinks = constructCourtAdditionalLinksEntity(courtEntity, additionalLinks);
        courtAdditionalLinkRepository.deleteCourtAdditionalLinksByCourtId(courtEntity.getId());
        List<AdditionalLink> newLinks = courtAdditionalLinkRepository.saveAll(newCourtAdditionalLinks)
            .stream()
            .map(CourtAdditionalLink::getAdditionalLink)
            .map(AdditionalLink::new)
            .collect(toList());
        adminAuditService.saveAudit(
            AuditType.findByName("Update court additional links"),
            courtEntity.getCourtAdditionalLinks()
                                  .stream()
                                  .map(CourtAdditionalLink::getAdditionalLink)
                                  .map(AdditionalLink::new)
                                  .collect(toList()),
            newLinks, slug);
        return newLinks;
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtAdditionalLink> constructCourtAdditionalLinksEntity(final Court courtEntity, final List<AdditionalLink> additionalLinks) {
        final List<CourtAdditionalLink> courtAdditionalLinks = new ArrayList<>();
        for (int i = 0; i < additionalLinks.size(); i++) {
            courtAdditionalLinks.add(new CourtAdditionalLink(courtEntity,
                                                             new uk.gov.hmcts.dts.fact.entity.AdditionalLink(
                                                                 additionalLinks.get(i).getUrl(),
                                                                 additionalLinks.get(i).getDisplayName(),
                                                                 additionalLinks.get(i).getDisplayNameCy()),
                                                             i));
        }
        return courtAdditionalLinks;
    }

    // to check java 17 compiles
    public IntStream getPseudoInts(String algorithm, int streamSize) {
        // returns an IntStream with size @streamSize of random numbers generated using the @algorithm
        // where the lower bound is 0 and the upper is 100 (exclusive)
        return RandomGeneratorFactory.of(algorithm)
            .create()
            .ints(streamSize, 0,100);
    }
}
