package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.OpeningTime;
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.OpeningTypeRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtOpeningTimeService {
    private final CourtRepository courtRepository;
    private final OpeningTypeRepository openingTypeRepository;

    @Autowired
    public AdminCourtOpeningTimeService(final CourtRepository courtRepository, final OpeningTypeRepository openingTypeRepository) {
        this.courtRepository = courtRepository;
        this.openingTypeRepository = openingTypeRepository;
    }

    public List<OpeningTime> getCourtOpeningTimesBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtOpeningTimes()
                .stream()
                .map(CourtOpeningTime::getOpeningTime)
                .map(OpeningTime::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    public List<OpeningTime> updateCourtOpeningTimes(final String slug, final List<OpeningTime> openingTimes) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return saveNewOpeningTimes(courtEntity, openingTimes);
    }

    public List<OpeningType> getAllCourtOpeningTypes() {
        return openingTypeRepository.findAll()
            .stream()
            .map(OpeningType::new)
            .collect(toList());
    }

    private List<OpeningTime> saveNewOpeningTimes(final Court courtEntity, final List<OpeningTime> openingTimes) {
        List<uk.gov.hmcts.dts.fact.entity.OpeningTime> openingTimeEntities = getNewOpeningTimes(openingTimes);
        List<CourtOpeningTime> courtOpeningTimeEntities = getNewCourtOpeningTimes(courtEntity, openingTimeEntities);

        if (CollectionUtils.isEmpty(courtEntity.getCourtOpeningTimes())) {
            courtEntity.setCourtOpeningTimes(courtOpeningTimeEntities);
        } else {
            courtEntity.getCourtOpeningTimes().clear();
            courtEntity.getCourtOpeningTimes().addAll(courtOpeningTimeEntities);
        }
        final Court courtWithUpdatedOpeningTime = courtRepository.save(courtEntity);

        return courtWithUpdatedOpeningTime.getCourtOpeningTimes()
            .stream()
            .map(CourtOpeningTime::getOpeningTime)
            .map(OpeningTime::new)
            .collect(toList());
    }

    private List<uk.gov.hmcts.dts.fact.entity.OpeningTime> getNewOpeningTimes(final List<OpeningTime> openingTimes) {
        return openingTimes.stream()
            .map(o -> new uk.gov.hmcts.dts.fact.entity.OpeningTime(o.getTypeId(), o.getHours()))
            .collect(toList());
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtOpeningTime> getNewCourtOpeningTimes(final Court court, final List<uk.gov.hmcts.dts.fact.entity.OpeningTime> openingTimes) {
        final List<CourtOpeningTime> courtOpeningTimes = new ArrayList<>();
        for (int i = 0; i < openingTimes.size(); i++) {
            courtOpeningTimes.add(new CourtOpeningTime(court, openingTimes.get(i), i));
        }
        return courtOpeningTimes;
    }
}
