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

import java.util.ArrayList;
import java.util.List;

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
        return courtRepository.findBySlug(slug)
            .map(c -> c.getAreasOfLaw()
                .stream()
                .map(AreaOfLaw::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional(rollbackFor = {RuntimeException.class})
    public List<AreaOfLaw> updateAreasOfLawForCourt(final String slug, final List<AreaOfLaw> areasOfLaw) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> newAreasOfLawList = getNewAreasOfLaw(areasOfLaw);
        List<CourtAreaOfLaw> newCourtAreasOfLawList = getNewCourtAreasOfLaw(courtEntity, newAreasOfLawList);

        courtAreaOfLawRepository.deleteCourtAreaOfLawByCourtId(courtEntity.getId());

        return courtAreaOfLawRepository
            .saveAll(newCourtAreasOfLawList)
            .stream()
            .map(CourtAreaOfLaw::getAreaOfLaw)
            .map(AreaOfLaw::new)
            .collect(toList());
    }

    private List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> getNewAreasOfLaw(final List<AreaOfLaw> areaOfLaw) {
        return areaOfLaw.stream()
            .map(e -> new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(e.getId(), e.getName()
            )).collect(toList());
    }

    private List<CourtAreaOfLaw> getNewCourtAreasOfLaw(final Court court, final List<uk.gov.hmcts.dts.fact.entity.AreaOfLaw> areasOfLaw) {
        final List<CourtAreaOfLaw> newCourtAreasOfLawList = new ArrayList<>();
        for (int i = 0; i < areasOfLaw.size(); i++) {
            newCourtAreasOfLawList.add(new CourtAreaOfLaw(areasOfLaw.get(i), court));
        }
        return newCourtAreasOfLawList;
    }
}






