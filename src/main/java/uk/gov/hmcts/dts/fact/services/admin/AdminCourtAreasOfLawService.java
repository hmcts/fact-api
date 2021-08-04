package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLaw;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtAreasOfLawService {

    private final CourtRepository courtRepository;
    private final CourtAreaOfLawRepository courtAreaOfLawRepository;

    @Autowired
    public AdminCourtAreasOfLawService(final CourtRepository courtRepository, final CourtAreaOfLawRepository courtAreaOfLawRepository) {
        this.courtRepository = courtRepository;
        this.courtAreaOfLawRepository = courtAreaOfLawRepository;
    }

    public List<AreaOfLaw> getCourtAreasOfLawBySlug(final String slug) {

        Court court = courtRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException(slug));
        List<CourtAreaOfLaw> courtAreaOfLawList = courtAreaOfLawRepository.getCourtAreaOfLawByCourtId(court.getId());
        return courtAreaOfLawList.stream()
            .map(aol -> new AreaOfLaw(aol.getAreaOfLaw(), aol.getSinglePointOfEntry())).collect(toList());
    }

    @Transactional()
    public List<AreaOfLaw> updateAreasOfLawForCourt(final String slug, final List<AreaOfLaw> areasOfLaw) {
        Map<Integer, Boolean> singlePointEntries =
            areasOfLaw.stream().collect(Collectors.toMap(AreaOfLaw::getId, AreaOfLaw::isSinglePointEntry));
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> newAreasOfLawList = getNewAreasOfLaw(areasOfLaw);
        List<CourtAreaOfLaw> newCourtAreasOfLawList = getNewCourtAreasOfLaw(courtEntity, newAreasOfLawList, singlePointEntries);

        courtAreaOfLawRepository.deleteCourtAreaOfLawByCourtId(courtEntity.getId());

        return courtAreaOfLawRepository
            .saveAll(newCourtAreasOfLawList)
            .stream()
            .map(aol -> new AreaOfLaw(aol.getAreaOfLaw(), aol.getSinglePointOfEntry()))
            .collect(toList());
    }

    private List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> getNewAreasOfLaw(final List<AreaOfLaw> areaOfLaw) {
        return areaOfLaw.stream()
            .map(e -> new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(e.getId(), e.getName())).collect(toList());
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtAreaOfLaw> getNewCourtAreasOfLaw(final Court court,
                                                       final List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> areasOfLaw,
                                                       Map<Integer, Boolean> singlePointEntries) {
        final List<CourtAreaOfLaw> newCourtAreasOfLawList = new ArrayList<>();
        for (uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw : areasOfLaw) {
            newCourtAreasOfLawList.add(new CourtAreaOfLaw(areaOfLaw, court, singlePointEntries.get(areaOfLaw.getId())));
        }
        return newCourtAreasOfLawList;
    }
}






