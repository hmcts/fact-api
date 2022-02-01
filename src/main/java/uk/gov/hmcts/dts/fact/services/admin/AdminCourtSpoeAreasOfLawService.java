package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLawSpoe;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.SpoeAreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawSpoeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtSpoeAreasOfLawService {

    private final CourtRepository courtRepository;
    private final AdminAuditService adminAuditService;
    private final CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository;

    @Autowired
    public AdminCourtSpoeAreasOfLawService(final CourtRepository courtRepository, final CourtAreaOfLawSpoeRepository courtAreaOfLawSpoeRepository, final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.courtAreaOfLawSpoeRepository = courtAreaOfLawSpoeRepository;
        this.adminAuditService = adminAuditService;
    }

    public List<SpoeAreaOfLaw> getAllSpoeAreasOfLaw() {
        return courtAreaOfLawSpoeRepository.findAll()
            .stream()
            .map(CourtAreaOfLawSpoe::getAreaOfLaw).distinct()
            .map(SpoeAreaOfLaw::new)
            .collect(toList());
    }

    public List<SpoeAreaOfLaw> getCourtSpoeAreasOfLawBySlug(final String slug) {

        Court court = courtRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException(slug));
        return  courtAreaOfLawSpoeRepository.getAllByCourtId(court.getId())
            .stream()
            .map(aol -> new SpoeAreaOfLaw(aol.getAreaOfLaw()))
            .collect(toList());
    }

    @Transactional()
    public List<SpoeAreaOfLaw> updateSpoeAreasOfLawForCourt(final String slug, final List<SpoeAreaOfLaw> areasOfLaw) {
        checkIfSpoeAreasOfLawHasDuplicateEntries(areasOfLaw);

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        final List<SpoeAreaOfLaw> originalCourtAol = getCourtSpoeAreasOfLawBySlug(slug);

        List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> newAreasOfLawSpoeList = getNewSpoeAreasOfLaw(areasOfLaw);
        List<CourtAreaOfLawSpoe> newCourtSpoeAreasOfLawList = getNewCourtSpoeAreasOfLaw(courtEntity, newAreasOfLawSpoeList);

        courtAreaOfLawSpoeRepository.deleteAllByCourtId(courtEntity.getId());

        List<SpoeAreaOfLaw> newSpoeAreaOfLawList = courtAreaOfLawSpoeRepository
            .saveAll(newCourtSpoeAreasOfLawList)
            .stream()
            .map(csaol -> new SpoeAreaOfLaw(csaol.getAreaOfLaw()))
            .collect(toList());

        adminAuditService.saveAudit(AuditType.findByName("Update court spoe areas of law"),
                                    originalCourtAol,
                                    newSpoeAreaOfLawList,
                                    slug);
        return newSpoeAreaOfLawList;
    }

    private List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> getNewSpoeAreasOfLaw(final List<SpoeAreaOfLaw> areasOfLaw) {
        return areasOfLaw.stream()
            .map(e -> new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(e.getId(), e.getName())).collect(toList());
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtAreaOfLawSpoe> getNewCourtSpoeAreasOfLaw(final Court court,
                                                               final List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> areasOfLaw) {
        final List<CourtAreaOfLawSpoe> newCourtSpoeAreasOfLawList = new ArrayList<>();
        for (uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLaw : areasOfLaw) {
            newCourtSpoeAreasOfLawList.add(new CourtAreaOfLawSpoe(areaOfLaw, court));
        }
        return newCourtSpoeAreasOfLawList;
    }

    private void checkIfSpoeAreasOfLawHasDuplicateEntries(final List<SpoeAreaOfLaw> areasOfLaw) {

        if (areasOfLaw.stream().distinct().count() != areasOfLaw.size()) {
            throw new DuplicatedListItemException("Duplicate single point of entries exist");
        }
    }
}






