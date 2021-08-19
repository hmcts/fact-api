package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtFacility;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Facility;
import uk.gov.hmcts.dts.fact.repositories.CourtFacilityRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.FacilityTypeRepository;

import java.util.List;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtFacilityService {
    private final CourtRepository courtRepository;
    private final CourtFacilityRepository courtFacilityRepository;
    private final FacilityTypeRepository facilityTypeRepository;

    @Autowired
    public AdminCourtFacilityService(final CourtRepository courtRepository, final CourtFacilityRepository courtFacilityRepository, final FacilityTypeRepository facilityTypeRepository) {
        this.courtRepository = courtRepository;
        this.courtFacilityRepository = courtFacilityRepository;
        this.facilityTypeRepository = facilityTypeRepository;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<Facility> getCourtFacilitiesBySlug(final String slug) {
        return  courtRepository.findBySlug(slug)
            .map(c -> c.getFacilities()
                .stream()
                .sorted(comparingInt(f -> f.getFacilityType().getOrder()))
                .map(Facility::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional()
    public List<Facility> updateCourtFacility(final String slug, final List<Facility> courtFacilities) {

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        return saveCourtFacilities(courtEntity, courtFacilities);
    }

    protected List<Facility> saveCourtFacilities(final Court courtEntity, final List<Facility> courtFacilities) {

        final List<uk.gov.hmcts.dts.fact.entity.Facility> facilitiesEntities = getNewFacilityEntity(courtFacilities);

        final List<CourtFacility> courtFacilitiesEntities = getNewCourtFacilityEntity(courtEntity, facilitiesEntities);

        final List<CourtFacility> existingList = getExistingCourtFacilities(courtEntity);

        //remove existing court facilities and add updated facilities
        courtFacilityRepository.deleteAll(existingList);

        return courtFacilityRepository
            .saveAll(courtFacilitiesEntities)
            .stream()
            .map(CourtFacility::getFacility)
            .sorted(comparingInt(f -> f.getFacilityType().getOrder()))
            .map(Facility::new)
            .collect(toList());
    }

    private List<uk.gov.hmcts.dts.fact.entity.Facility> getNewFacilityEntity(final List<Facility> facilities) {

        return facilities.stream()
            .map(f -> new uk.gov.hmcts.dts.fact.entity.Facility(f.getName(),f.getDescription(), f.getDescriptionCy(),
                                                                facilityTypeRepository.findByName(f.getName()).orElse(null)))
            .collect(toList());
    }

    private List<CourtFacility> getNewCourtFacilityEntity(final Court courtEntity, List<uk.gov.hmcts.dts.fact.entity.Facility> facilities) {

        return facilities.stream()
            .map(f -> new CourtFacility(courtEntity,f)).collect(toList());
    }

    private List<CourtFacility> getExistingCourtFacilities(final Court courtEntity) {
        return courtFacilityRepository.findByCourtId(courtEntity.getId()).stream().collect(toList());
    }

}
