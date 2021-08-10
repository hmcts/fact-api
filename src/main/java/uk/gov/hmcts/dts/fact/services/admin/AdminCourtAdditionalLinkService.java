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
import uk.gov.hmcts.dts.fact.repositories.SidebarLocationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.SidebarLocation.FIND_OUT_MORE_ABOUT;
import static uk.gov.hmcts.dts.fact.util.SidebarLocation.THIS_LOCATION_HANDLES;

@Service
public class AdminCourtAdditionalLinkService {
    private final CourtRepository courtRepository;
    private final CourtAdditionalLinkRepository courtAdditionalLinkRepository;
    private final SidebarLocationRepository sidebarLocationRepository;

    public AdminCourtAdditionalLinkService(final CourtRepository courtRepository, final CourtAdditionalLinkRepository courtAdditionalLinkRepository, final SidebarLocationRepository sidebarLocationRepository) {
        this.courtRepository = courtRepository;
        this.courtAdditionalLinkRepository = courtAdditionalLinkRepository;
        this.sidebarLocationRepository = sidebarLocationRepository;
    }

    public List<AdditionalLink> getCourtAdditionalLinksBySlug(final String slug) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        return courtEntity.isInPerson()
            ? getCourtAdditionalLinksFromRepository(courtEntity, link -> FIND_OUT_MORE_ABOUT.getName().equalsIgnoreCase(link.getLocation().getName()))
            : emptyList();
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

        // The admin user can only update additional links in the 'find out more about' section
        final String sidebarName = FIND_OUT_MORE_ABOUT.getName();
        final SidebarLocation sidebarLocation = sidebarLocationRepository.findSidebarLocationByName(sidebarName)
            .orElseThrow(() -> new NotFoundException(sidebarName));

        final List<CourtAdditionalLink> newCourtAdditionalLinks = constructCourtAdditionalLinksEntity(courtEntity, additionalLinks, sidebarLocation);
        courtAdditionalLinkRepository.deleteCourtAdditionalLinksByCourtIdAndAdditionalLinkLocationId(courtEntity.getId(),
                                                                                                     sidebarLocation.getId());
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
    private List<CourtAdditionalLink> constructCourtAdditionalLinksEntity(final Court courtEntity, final List<AdditionalLink> additionalLinks, final SidebarLocation sidebarLocation) {
        final List<CourtAdditionalLink> courtAdditionalLinks = new ArrayList<>();
        for (int i = 0; i < additionalLinks.size(); i++) {
            courtAdditionalLinks.add(new CourtAdditionalLink(courtEntity,
                                                             new uk.gov.hmcts.dts.fact.entity.AdditionalLink(
                                                                 additionalLinks.get(i).getUrl(),
                                                                 additionalLinks.get(i).getDisplayName(),
                                                                 additionalLinks.get(i).getDisplayNameCy(),
                                                                 additionalLinks.get(i).getType(),
                                                                 sidebarLocation),
                                                             i));
        }
        return courtAdditionalLinks;
    }
}
