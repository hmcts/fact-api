package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Facility;
import uk.gov.hmcts.dts.fact.repositories.CourtFacilityRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.FacilityTypeRepository;

import java.util.List;

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
                .map(Facility::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional()
    public List<Facility> updateCourtFacility(final String slug, final List<Facility> courtFacilities) {

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        return saveCourtFacilities(courtEntity,courtFacilities);
    }


    protected List<Facility> saveCourtFacilities(final Court courtEntity, final List<Facility> courtFacilities) {

        final List<uk.gov.hmcts.dts.fact.entity.Facility> courtFacilitiesEntities = getNewCourtFacilityEntity(courtFacilities);

        //remove existing court facilities and add updated facilities
        courtFacilityRepository.deleteAll(courtEntity.getFacilities());
        courtFacilityRepository.saveAll(courtFacilitiesEntities);

        if (courtEntity.getFacilities().isEmpty()) {
            courtEntity.setFacilities(courtFacilitiesEntities);
        } else {
            courtEntity.getFacilities().clear();
            courtEntity.getFacilities().addAll(courtFacilitiesEntities);
        }

        courtRepository.save(courtEntity);

        return courtEntity.getFacilities().stream()
            .map(Facility::new)
            .collect(toList());
    }

    private List<uk.gov.hmcts.dts.fact.entity.Facility> getNewCourtFacilityEntity(final List<Facility> facilities) {

        return facilities.stream()
            .map(f -> new uk.gov.hmcts.dts.fact.entity.Facility(f.getName(),f.getDescription(),
                                                                f.getDescriptionCy(),
                                                                facilityTypeRepository.findByName(f.getName()).orElse(null)))
            .collect(toList());
    }

}
