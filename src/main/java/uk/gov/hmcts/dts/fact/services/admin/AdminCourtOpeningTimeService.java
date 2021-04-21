package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.OpeningTime;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtOpeningTimeService {
    private final CourtRepository courtRepository;

    @Autowired
    public AdminCourtOpeningTimeService(final CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
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

    private List<OpeningTime> saveNewOpeningTimes(final Court courtEntity, final List<OpeningTime> openingTimes) {
        List<uk.gov.hmcts.dts.fact.entity.OpeningTime> openingTimesEntity = getNewOpeningTimesEntity(openingTimes);
        List<CourtOpeningTime> courtOpeningTimesEntity = getNewCourtOpeningTimesEntity(courtEntity, openingTimesEntity);

        if (CollectionUtils.isEmpty(courtEntity.getCourtOpeningTimes())) {
            courtEntity.setCourtOpeningTimes(courtOpeningTimesEntity);
        } else {
            courtEntity.getCourtOpeningTimes().clear();
            courtEntity.getCourtOpeningTimes().addAll(courtOpeningTimesEntity);
        }
        final Court courtWithUpdatedOpeningTime = courtRepository.save(courtEntity);

        return courtWithUpdatedOpeningTime.getCourtOpeningTimes()
            .stream()
            .map(CourtOpeningTime::getOpeningTime)
            .map(OpeningTime::new)
            .collect(toList());
    }

    private List<uk.gov.hmcts.dts.fact.entity.OpeningTime> getNewOpeningTimesEntity(final List<OpeningTime> openingTimes) {
        return openingTimes.stream()
            .map(o -> new uk.gov.hmcts.dts.fact.entity.OpeningTime(o.getType(), o.getTypeCy(), o.getHours()))
            .collect(toList());
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtOpeningTime> getNewCourtOpeningTimesEntity(final Court court, final List<uk.gov.hmcts.dts.fact.entity.OpeningTime> openingTimes) {
        final List<CourtOpeningTime> courtOpeningTimes = new ArrayList<>();
        for (int i = 0; i < openingTimes.size(); i++) {
            courtOpeningTimes.add(new CourtOpeningTime(court, openingTimes.get(i), i));
        }
        return courtOpeningTimes;
    }
}
