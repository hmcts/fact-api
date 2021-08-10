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
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.SidebarLocation.FIND_OUT_MORE_ABOUT;
import static uk.gov.hmcts.dts.fact.util.SidebarLocation.THIS_LOCATION_HANDLES;

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
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return getCourtAdditionalLinksFromRepository(courtEntity, link -> true);
    }

    public List<AdditionalLink> getCourtCasesHeardAdditionalLinks(final Court court) {
        if (court.isInPerson()) {
            return getCourtAdditionalLinksFromRepository(court, link -> THIS_LOCATION_HANDLES.getName().equalsIgnoreCase(link.getLocation().getName()));
        }
        return getCourtAdditionalLinksFromRepository(court, link -> FIND_OUT_MORE_ABOUT.getName().equalsIgnoreCase(link.getLocation().getName()));
    }

    @Transactional
    public List<AdditionalLink> updateCourtAdditionalLinks(final String slug, final List<AdditionalLink> additionalLinks) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        final List<CourtAdditionalLink> newCourtAdditionalLinks = constructCourtAdditionalLinksEntity(courtEntity, additionalLinks);
        courtAdditionalLinkRepository.deleteCourtAdditionalLinksByCourtId(courtEntity.getId());

        return courtAdditionalLinkRepository.saveAll(newCourtAdditionalLinks)
            .stream()
            .map(a -> a.getAdditionalLink())
            .map(AdditionalLink::new)
            .collect(toList());
    }

    private List<AdditionalLink> getCourtAdditionalLinksFromRepository(final Court court, final Predicate<uk.gov.hmcts.dts.fact.entity.AdditionalLink> filter) {
        return court.getCourtAdditionalLinks()
            .stream()
            .map(a -> a.getAdditionalLink())
            .filter(filter)
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
                                                                 additionalLinks.get(i).getUrlDescription(),
                                                                 additionalLinks.get(i).getUrlDescriptionCy(),
                                                                 additionalLinks.get(i).getType(),
                                                                 sidebarLocationMap.get(additionalLinks.get(i).getSidebarLocationId())),
                                                             i));
        }
        return courtAdditionalLinks;
    }
}
