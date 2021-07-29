package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Facility;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.FacilityTypeRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtFacilityService {
    private final CourtRepository courtRepository;
    private final FacilityTypeRepository facilityTypeRepository;

    @Autowired
    public AdminCourtFacilityService(final CourtRepository courtRepository, final FacilityTypeRepository facilityTypeRepository) {
        this.courtRepository = courtRepository;
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
    public List<Facility> updateCourtFacility(final String slug, final Facility facility) {

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        List<Facility> courtFacilities = getCourtFacilitiesBySlug(slug);

        //remove the old court facility details and replace with latest
        courtFacilities.removeIf(cf -> cf.getName().equals(facility.getName()));
        courtFacilities.add(facility);

        return saveCourtFacilities(courtEntity,courtFacilities);
    }

    @Transactional()
    public List<Facility> addCourtFacility(final String slug, final Facility facility) {

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        List<Facility> courtFacilities = getCourtFacilitiesBySlug(slug);

        checkIfCourtFacilityAlreadyExists(courtFacilities,facility);

        courtFacilities.add(facility);

        return saveCourtFacilities(courtEntity,courtFacilities);
    }


    @Transactional()
    public List<Facility> deleteCourtFacility(final String slug, final Facility facility) {

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        List<Facility> courtFacilities = getCourtFacilitiesBySlug(slug);

        courtFacilities.remove(facility);

        return saveCourtFacilities(courtEntity,courtFacilities);
    }


    protected List<Facility> saveCourtFacilities(final Court courtEntity, final List<Facility> courtFacilities) {

        final List<uk.gov.hmcts.dts.fact.entity.Facility> courtFacilitiesEntities = getNewCourtFacilityEntity(courtFacilities);

        if (courtEntity.getFacilities() == null) {
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

    private void checkIfCourtFacilityAlreadyExists(final List<Facility> existingCourtFacilities, final Facility facility) {

        if (!existingCourtFacilities.isEmpty()
            && existingCourtFacilities.stream().anyMatch(cf -> cf.getName().equals(facility.getName()))) {
            throw new DuplicatedListItemException("Facility already exists: " + facility.getName());
        }
    }
}
