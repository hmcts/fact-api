package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAdditionalLink;
import uk.gov.hmcts.dts.fact.entity.SidebarLocation;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AdditionalLink;
import uk.gov.hmcts.dts.fact.repositories.CourtAdditionalLinkRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminSidebarLocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtAdditionalLinkService {
    private final CourtRepository courtRepository;
    private final CourtAdditionalLinkRepository courtAdditionalLinkRepository;
    private final AdminSidebarLocationService sidebarLocationService;

    public AdminCourtAdditionalLinkService(final CourtRepository courtRepository, final CourtAdditionalLinkRepository courtAdditionalLinkRepository, final AdminSidebarLocationService sidebarLocationService) {
        this.courtRepository = courtRepository;
        this.courtAdditionalLinkRepository = courtAdditionalLinkRepository;
        this.sidebarLocationService = sidebarLocationService;
    }

    public List<AdditionalLink> getCourtAdditionalLinksBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtAdditionalLinks()
                .stream()
                .map(a -> a.getAdditionalLink())
                .map(AdditionalLink::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional
    public List<AdditionalLink> updateCourtAdditionalLinks(final String slug, final List<AdditionalLink> additionalLinks) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        final List<CourtAdditionalLink> newCourtAdditionalLinks = constructCourtAdditionalLinksEntity(courtEntity, additionalLinks);
        courtAdditionalLinkRepository.deleteAll(courtEntity.getCourtAdditionalLinks());

        return courtAdditionalLinkRepository.saveAll(newCourtAdditionalLinks)
            .stream()
            .map(a -> a.getAdditionalLink())
            .map(AdditionalLink::new)
            .collect(toList());
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.DataflowAnomalyAnalysis"})
    private List<CourtAdditionalLink> constructCourtAdditionalLinksEntity(final Court courtEntity, final List<AdditionalLink> additionalLinks) {
        final Map<Integer, SidebarLocation> sidebarLocationMap = sidebarLocationService.getSidebarLocationMap();
        final List<CourtAdditionalLink> courtAdditionalLinks = new ArrayList<>();
        for (int i = 0; i < additionalLinks.size(); i++) {
            courtAdditionalLinks.add(new CourtAdditionalLink(courtEntity,
                                                             new uk.gov.hmcts.dts.fact.entity.AdditionalLink(
                                                                 additionalLinks.get(i).getUrl(),
                                                                 additionalLinks.get(i).getDescription(),
                                                                 additionalLinks.get(i).getDescriptionCy(),
                                                                 sidebarLocationMap.get(additionalLinks.get(i).getSidebarLocationId())
                                                             ), i));
        }
        return courtAdditionalLinks;
    }
}
