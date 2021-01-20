package uk.gov.hmcts.dts.fact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtForDownload;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtInfoUpdate;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Service
public class AdminService {

    private final CourtRepository courtRepository;

    private final RolesProvider rolesProvider;

    @Autowired
    public AdminService(final CourtRepository courtRepository, final RolesProvider rolesProvider) {
        this.courtRepository = courtRepository;
        this.rolesProvider = rolesProvider;
    }

    public List<CourtReference> getAllCourtReferences() {
        return courtRepository
            .findAll()
            .stream()
            .map(CourtReference::new)
            .collect(toList());
    }

    public List<CourtForDownload> getAllCourtsForDownload() {
        return courtRepository
            .findAll()
            .stream()
            .map(CourtForDownload::new)
            .sorted(Comparator.comparing(CourtForDownload::getName))
            .collect(toList());
    }

    public Court getCourtBySlug(String slug) {
        return courtRepository
            .findBySlug(slug)
            .map(Court::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    public uk.gov.hmcts.dts.fact.entity.Court getCourtEntityBySlug(String slug) {
        return courtRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException(slug));
    }

    public Court save(String slug, Court court) {
        uk.gov.hmcts.dts.fact.entity.Court courtEntity = getCourtEntityBySlug(slug);
        courtEntity.setAlert(court.getAlert());
        courtEntity.setAlertCy(court.getAlertCy());

        if (rolesProvider.getRoles().contains("fact-super-admin")) {
            courtEntity.setDisplayed(court.getOpen());
            courtEntity.setInfo(court.getInfo());
            courtEntity.setInfoCy(court.getInfoCy());
            if (courtEntity.getInPerson() != null && courtEntity.getInPerson().getIsInPerson()) {
                courtEntity.getInPerson().setAccessScheme(court.getAccessScheme());
            }
        }


        final List<OpeningTime> openingTimes =
            ofNullable(court.getOpeningTimes())
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(o -> new OpeningTime(o.getType(), o.getHours()))
                .collect(toList());

        List<CourtOpeningTime> courtOpeningTimes = new ArrayList<>();
        for (int i = 0; i < openingTimes.size(); i++) {
            @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
            CourtOpeningTime courtOpeningTime = new CourtOpeningTime();
            courtOpeningTime.setCourt(courtEntity);
            courtOpeningTime.setOpeningTime(openingTimes.get(i));
            courtOpeningTime.setSort(i);
            courtOpeningTimes.add(courtOpeningTime);
        }
        if (courtEntity.getCourtOpeningTimes() == null) {
            courtEntity.setCourtOpeningTimes(courtOpeningTimes);
        } else {
            courtEntity.getCourtOpeningTimes().clear();
            courtEntity.getCourtOpeningTimes().addAll(courtOpeningTimes);
        }

        uk.gov.hmcts.dts.fact.entity.Court updatedCourt = courtRepository.save(courtEntity);
        return new Court(updatedCourt);
    }

    @Transactional
    public void updateMultipleCourtsInfo(CourtInfoUpdate info) {
        courtRepository.updateInfoForSlugs(info.getCourts(), info.getInfo(), info.getInfoCy());
    }
}
